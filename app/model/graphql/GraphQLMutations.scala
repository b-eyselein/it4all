package model.graphql

import java.time.Clock

import com.github.t3hnar.bcrypt._
import model.mongo.MongoClientQueries
import model.tools.ToolList
import model.{JsonProtocols, _}
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
 
  private def register(registerValues: RegisterValues): Future[Option[String]] =
    if (registerValues.isInvalid) {
      Future.successful(None)
    } else {
      futureUserByUsername(registerValues.username).flatMap {
        case Some(_) => Future.successful(None)
        case None =>
          val newUser = User(registerValues.username, Some(registerValues.firstPassword.bcrypt))

          futureInsertUser(newUser).map {
            case false => None
            case true  => Some(newUser.username)
          }
      }
    }

  private def authenticate(credentials: UserCredentials): Future[Option[LoggedInUserWithToken]] =
    futureUserByUsername(credentials.username).map { maybeUser =>
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
    fields = ToolList.tools.map { tool =>
      def correct(
        user: LoggedInUser,
        exercise: Exercise[tool.ExContentType],
        part: tool.PartType,
        solution: tool.SolType
      ): Future[tool.ResType] = {

        for {
          nextUserSolutionId <- nextUserSolutionId(exercise, part)(tool.jsonFormats.partTypeFormat)

          solutionSaved <- insertSolution(
            UserSolution.forExercise(nextUserSolutionId, exercise, user.username, solution, part),
            tool.jsonFormats.userSolutionFormat
          )

          result <- tool.correctAbstract(user, solution, exercise, part)

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
        } yield result.updateSolutionSaved(solutionSaved)
      }

      val toolExerciseMutationsType = ObjectType(
        s"${tool.id.capitalize}ExerciseMutations",
        fields[Unit, (LoggedInUser, Exercise[tool.ExContentType])](
          Field(
            "correct",
            tool.graphQlModels.toolAbstractResultTypeInterfaceType,
            arguments = tool.graphQlModels.partTypeInputArg :: tool.graphQlModels.solTypeInputArg :: Nil,
            resolve = context =>
              correct(
                context.value._1,
                context.value._2,
                context.arg(tool.graphQlModels.partTypeInputArg),
                context.arg(tool.graphQlModels.solTypeInputArg)
              )
          )
        )
      )

      Field(
        s"${tool.id}Exercise",
        OptionType(toolExerciseMutationsType),
        arguments = collIdArgument :: exIdArgument :: Nil,
        resolve = (context: Context[Unit, LoggedInUser]) => {
          val collId = context.arg(collIdArgument)
          val exId   = context.arg(exIdArgument)

          futureExerciseById(tool.id, collId, exId, tool.jsonFormats.exerciseFormat)
            .map(maybeExercise => maybeExercise.map(exercise => (context.value, exercise)))
        }
      )
    }
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
    )
  )

}
