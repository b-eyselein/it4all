package model.graphql

import java.time.Clock

import com.github.t3hnar.bcrypt._
import model._
import model.json.JsonProtocols
import model.tools.ToolList
import pdi.jwt.JwtSession
import play.api.Configuration
import play.api.libs.json._
import sangria.marshalling.playJson._
import sangria.schema._

import scala.concurrent.Future

trait GraphQLMutations extends CollectionGraphQLModel with GraphQLArguments with MongoClientQueries {

  private val userFieldName = "user"
  private val clock         = Clock.systemDefaultZone()

  private implicit val userFormat: OFormat[LoggedInUser] = JsonProtocols.loggedInUserFormat

  protected val configuration: Configuration

  protected def createJwtSession(user: LoggedInUser): JwtSession = {
    JwtSession()(configuration, clock) + (userFieldName, user)
  }

  protected def writeJsonWebToken(user: LoggedInUserWithToken): JsValue =
    JsonProtocols.loggedInUserWithTokenFormat.writes(user)

  protected def deserializeJwt(jwtString: String): Option[LoggedInUser] =
    JwtSession
      .deserialize(jwtString)(configuration, clock)
      .getAs[LoggedInUser](userFieldName)

  private val correctionFields = ToolList.tools.map[Field[Unit, LoggedInUser]] { tool =>
    implicit val solTypeFormat: Format[tool.SolType] = tool.jsonFormats.solutionFormat
    implicit val partFormat: Format[tool.PartType]   = tool.jsonFormats.partTypeFormat

    val SolTypeInputArg: Argument[tool.SolType] = Argument("solution", tool.graphQlModels.SolTypeInputType)
    val PartTypeInputArg                        = Argument("part", tool.graphQlModels.partEnumType)

    Field(
      s"correct${tool.id.capitalize}",
      tool.graphQlModels.toolAbstractResultTypeInterfaceType,
      arguments = collIdArgument :: exIdArgument :: PartTypeInputArg :: SolTypeInputArg :: Nil,
      resolve = context => {

        val user     = context.value
        val part     = context.arg(PartTypeInputArg)
        val solution = context.arg(SolTypeInputArg)

        getExercise(tool.id, context.arg(collIdArgument), context.arg(exIdArgument), tool.jsonFormats.exerciseFormat)
          .flatMap {
            case Some(exercise) =>
              for {
                nextUserSolutionId <- nextUserSolutionId(exercise, part)
                solutionSaved <- insertSolution(
                  UserSolution.forExercise(nextUserSolutionId, exercise, user.username, solution, part),
                  tool.jsonFormats.userSolutionFormat
                )
                result <- tool.correctAbstract(user, solution, exercise, part, solutionSaved)

                _ <-
                  if (result.isCompletelyCorrect) {
                    Future
                      .sequence(
                        exercise.topicsWithLevels.map { topicWithLevel =>
                          updateUserProficiencies(user.username, exercise, topicWithLevel)
                        }
                      )
                      .recover { _ => println("Could not update user proficiencies!") }
                  } else {
                    Future.successful(())
                  }
              } yield result

            case _ => ???
          }
      }
    )
  }

  private def register(registerValues: RegisterValues): Future[Option[String]] =
    if (registerValues.isInvalid) {
      Future.successful(None)
    } else {
      getUser(registerValues.username).flatMap {
        case Some(_) => Future.successful(None)
        case None =>
          val newUser = User(registerValues.username, Some(registerValues.firstPassword.bcrypt))

          insertUser(newUser).map {
            case false => None
            case true  => Some(newUser.username)
          }
      }
    }

  private def authenticate(credentials: UserCredentials): Future[Option[LoggedInUserWithToken]] =
    getUser(credentials.username).map { maybeUser =>
      for {
        user: User      <- maybeUser
        pwHash: String  <- user.pwHash
        pwOkay: Boolean <- credentials.password.isBcryptedSafe(pwHash).toOption
        maybeUser <-
          if (pwOkay) {
            val loggedInUser = LoggedInUser(user.username, user.isAdmin)

            Some(LoggedInUserWithToken(loggedInUser, createJwtSession(loggedInUser).serialize))
          } else None

      } yield maybeUser
    }

  private val loggedInUserMutationType: ObjectType[Unit, LoggedInUser] = ObjectType(
    "UserMutations",
    correctionFields
  )

  protected val MutationType: ObjectType[Unit, Unit] = ObjectType(
    "Mutation",
    fields = fields[Unit, Unit](
      Field(
        "register",
        OptionType(StringType),
        arguments = registerValuesArgument :: Nil,
        resolve = context => register(context.arg(registerValuesArgument))
      ),
      Field(
        "login",
        OptionType(loggedInUserWithTokenType),
        arguments = userCredentialsArgument :: Nil,
        resolve = context => authenticate(context.arg(userCredentialsArgument))
      ),
      Field(
        "me",
        OptionType(loggedInUserMutationType),
        arguments = userJwtArgument :: Nil,
        resolve = context => deserializeJwt(context.arg(userJwtArgument))
      )
    ) /*++ correctionFields */
  )

}
