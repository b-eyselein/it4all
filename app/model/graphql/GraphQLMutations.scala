package model.graphql

import com.github.t3hnar.bcrypt._
import model._
import model.mongo.MongoClientQueries
import model.result.{BasicExercisePartResult, CorrectionResult}
import model.tools.ToolList
import play.api.libs.json._
import sangria.macros.derive._
import sangria.marshalling.playJson._
import sangria.schema._

import scala.collection.mutable.{Map => MutableMap}
import scala.concurrent.Future
import scala.util.{Failure, Success}

trait GraphQLMutations extends ExerciseGraphQLModels with JwtHelpers {

  protected val jwtHashesToClaim: MutableMap[String, LoginResult] = MutableMap.empty

  private val loginResultType: ObjectType[Unit, LoginResult] = deriveObjectType()

  private def register(tableDefs: TableDefs, registerValues: RegisterValues): Future[Option[String]] = if (registerValues.isInvalid) {
    // TODO: return error?
    Future.successful(None)
  } else {
    for {
      maybeUser <- tableDefs.futureUserByUsername(registerValues.username)
      newUser <- maybeUser match {
        case Some(_) => Future.successful(None)
        case None =>
          val RegisterValues(username, firstPassword, _) = registerValues

          tableDefs
            .futureInsertUser(username, Some(firstPassword.boundedBcrypt))
            .map { case User(username, _) => Some(username) }
      }
    } yield newUser
  }

  private def authenticate(tableDefs: TableDefs, credentials: UserCredentials): Future[Option[LoginResult]] =
    tableDefs.futureUserByUsername(credentials.username).map { maybeUser =>
      for {
        user   <- maybeUser
        pwHash <- user.pwHash
        pwOkay <- credentials.password.isBcryptedSafeBounded(pwHash).toOption
        maybeUser <-
          if (pwOkay) {
            Some(LoginResult(user.username, createJwtSession(user.username)))
          } else {
            None
          }

      } yield maybeUser
    }

  private def updateAllUserProficiencies[EC <: ExerciseContent](
    mongoQueries: MongoClientQueries,
    username: String,
    exercise: Exercise[EC]
  ): Future[Boolean] = Future
    .sequence {
      exercise.topicsWithLevels.map { topicWithLevel =>
        mongoQueries
          .updateUserProficiency(username, exercise, topicWithLevel)
          .recover { _ => false }
      }
    }
    .map { updateResults => updateResults.forall(identity) }

  private val exerciseCorrectionFields: Seq[Field[GraphQLContext, Unit]] = ToolList.tools.map[Field[GraphQLContext, Unit]] { tool =>
    type P = tool.PartType
    type S = tool.SolutionInputType
    type E = tool.ExContentType
    type R = tool.ResType

    implicit val partFormat: Format[P]    = tool.jsonFormats.partTypeFormat
    implicit val solTypeFormat: Format[S] = tool.jsonFormats.solutionInputFormat

    val partTypeInputArg: Argument[P] = Argument("part", tool.graphQlModels.partEnumType)
    val solTypeInputArg: Argument[S]  = Argument("solution", tool.graphQlModels.solutionInputType)

    def correct(mongoQueries: MongoClientQueries, user: User, ex: Exercise[E], part: P, solution: S): Future[CorrectionResult[R]] = tool
      .correctAbstract(user, solution, ex, part)
      .transform { // flatten Future[Try[_]] to Future[_] with implicit Try[_]
        case Failure(exception)          => Failure(exception)
        case Success(Failure(exception)) => Failure(exception)
        case Success(success)            => success
      }
      .flatMap { result =>
        for {
          nextUserSolutionId <- mongoQueries.nextUserSolutionId(ex, part)

          solutionSaved <- mongoQueries.insertSolution(
            UserSolution[S, P](
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

          resultSaved <- mongoQueries.futureUpsertExerciseResult(
            BasicExercisePartResult.forExerciseAndResult(user.username, ex, part.id, result)
          )

          proficienciesUpdated <-
            if (result.isCompletelyCorrect) updateAllUserProficiencies(mongoQueries, user.username, ex).map(Some.apply)
            else Future.successful(None)

        } yield CorrectionResult(solutionSaved, resultSaved, proficienciesUpdated, result)
      }

    val correctionResultType: ObjectType[Unit, CorrectionResult[R]] = deriveObjectType(
      ObjectTypeName(s"${tool.id.capitalize}CorrectionResult"),
      ReplaceField(
        "result",
        Field("result", tool.graphQlModels.resultType, resolve = _.value.result)
      )
    )

    val toolExerciseMutationsType = ObjectType(
      s"${tool.id.capitalize}ExerciseMutations",
      fields[GraphQLContext, Exercise[tool.ExContentType]](
        Field(
          "correct",
          correctionResultType,
          arguments = partTypeInputArg :: solTypeInputArg :: Nil,
          resolve = context =>
            context.ctx.loggedInUser match {
              case None => Future.failed(new Exception("User is not logged in!"))
              case Some(loggedInUser) =>
                correct(context.ctx.mongoQueries, loggedInUser, context.value, context.arg(partTypeInputArg), context.arg(solTypeInputArg))
            }
        )
      )
    )

    Field(
      s"${tool.id}Exercise",
      OptionType(toolExerciseMutationsType),
      arguments = collIdArgument :: exIdArgument :: Nil,
      resolve = context => context.ctx.mongoQueries.futureExerciseById(tool, context.arg(collIdArgument), context.arg(exIdArgument))
    )
  }

  protected val MutationType: ObjectType[GraphQLContext, Unit] = ObjectType(
    "Mutation",
    fields = fields[GraphQLContext, Unit](
      Field(
        "register",
        OptionType(StringType),
        arguments = registerValuesArgument :: Nil,
        resolve = context => register(context.ctx.tableDefs, context.arg(registerValuesArgument))
      ),
      Field(
        "login",
        OptionType(loginResultType),
        arguments = userCredentialsArgument :: Nil,
        resolve = context => authenticate(context.ctx.tableDefs, context.arg(userCredentialsArgument))
      ),
      Field(
        "claimLtiWebToken",
        OptionType(loginResultType),
        arguments = ltiUuidArgument :: Nil,
        resolve = context => jwtHashesToClaim.remove(context.arg(ltiUuidArgument))
      )
    ) ++ exerciseCorrectionFields
  )

}
