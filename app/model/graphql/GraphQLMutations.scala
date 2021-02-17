package model.graphql

import com.github.t3hnar.bcrypt._
import model._
import model.mongo.MongoClientQueries
import model.result.{BasicExercisePartResult, CorrectionResult}
import model.tools.ToolList
import pdi.jwt.JwtSession
import play.api.Configuration
import play.api.libs.json._
import sangria.macros.derive._
import sangria.marshalling.playJson._
import sangria.schema._

import java.time.Clock
import scala.concurrent.Future
import scala.util.{Failure, Success}

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
          val newUser = User(registerValues.username, Some(registerValues.firstPassword.boundedBcrypt))

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
        pwOkay: Boolean <- credentials.password.isBcryptedSafeBounded(pwHash).toOption
        maybeUser <-
          if (pwOkay) {
            val loggedInUser = LoggedInUser(user.username, user.isAdmin)

            Some(LoggedInUserWithToken(loggedInUser, createJwtSession(loggedInUser).serialize))
          } else None

      } yield maybeUser
    }

  private def updateAllUserProficiencies[EC <: ExerciseContent](
    username: String,
    exercise: Exercise[EC]
  ): Future[Boolean] =
    Future
      .sequence {
        exercise.topicsWithLevels.map { topicWithLevel =>
          updateUserProficiency(username, exercise, topicWithLevel)
            .recover { _ => false }
        }
      }
      .map { updateResults => updateResults.forall(identity) }

  private val loggedInUserMutationType: ObjectType[Unit, LoggedInUser] = ObjectType(
    "UserMutations",
    fields = ToolList.tools.map { tool =>
      type P = tool.PartType
      type S = tool.SolType
      type E = tool.ExContentType
      type R = tool.ResType

      implicit val partFormat: Format[P]    = tool.jsonFormats.partTypeFormat
      implicit val solTypeFormat: Format[S] = tool.jsonFormats.solutionFormat

      val partTypeInputArg: Argument[P] = Argument("part", tool.graphQlModels.partEnumType)
      val solTypeInputArg: Argument[S]  = Argument("solution", tool.graphQlModels.SolTypeInputType)

      def correct(user: LoggedInUser, ex: Exercise[E], part: P, solution: S): Future[CorrectionResult[R]] = tool
        .correctAbstract(user, solution, ex, part)
        .transform {
          // flatten Future[Try[_]] to Future[_] with implicit Try[_]
          case Failure(exception)          => Failure(exception)
          case Success(Failure(exception)) => Failure(exception)
          case Success(success)            => success
        }
        .flatMap { result =>
          for {
            nextUserSolutionId <- nextUserSolutionId(ex, part)

            solutionSaved <- insertSolution(
              UserSolution(
                nextUserSolutionId,
                ex.exerciseId,
                ex.collectionId,
                ex.toolId,
                user.username,
                solution,
                part
              ),
              tool.jsonFormats.userSolutionFormat
            )

            resultSaved <- futureUpsertExerciseResult(
              BasicExercisePartResult.forExerciseAndResult(user.username, ex, part.id, result)
            )

            proficienciesUpdated <-
              if (result.isCompletelyCorrect) updateAllUserProficiencies(user.username, ex).map(Some.apply)
              else Future.successful(None)

          } yield CorrectionResult(solutionSaved, resultSaved, proficienciesUpdated, result)
        }

      val correctionResultType: ObjectType[Unit, CorrectionResult[R]] = deriveObjectType(
        ObjectTypeName(s"${tool.id.capitalize}CorrectionResult"),
        ReplaceField(
          "result",
          Field("result", tool.graphQlModels.toolAbstractResultTypeInterfaceType, resolve = _.value.result)
        )
      )

      val toolExerciseMutationsType = ObjectType(
        s"${tool.id.capitalize}ExerciseMutations",
        fields[Unit, (LoggedInUser, Exercise[tool.ExContentType])](
          Field(
            "correct",
            correctionResultType,
            arguments = partTypeInputArg :: solTypeInputArg :: Nil,
            resolve = context =>
              correct(context.value._1, context.value._2, context.arg(partTypeInputArg), context.arg(solTypeInputArg))
          )
        )
      )

      Field(
        s"${tool.id}Exercise",
        OptionType(toolExerciseMutationsType),
        arguments = collIdArgument :: exIdArgument :: Nil,
        resolve = (context: Context[Unit, LoggedInUser]) =>
          futureExerciseById(tool, context.arg(collIdArgument), context.arg(exIdArgument))
            .map(maybeExercise => maybeExercise.map(exercise => (context.value, exercise)))
      )
    }
  )

  protected val MutationType: ObjectType[GraphQLContext, Unit] = ObjectType(
    "Mutation",
    fields = fields[GraphQLContext, Unit](
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
      Field("me", OptionType(loggedInUserMutationType), resolve = context => context.ctx.loggedInUser)
    )
  )

}
