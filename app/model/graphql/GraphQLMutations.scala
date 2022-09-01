package model.graphql

import com.github.t3hnar.bcrypt._
import model._
import model.tools.{ToolList, ToolWithParts, ToolWithoutParts}
import play.api.libs.json._
import sangria.macros.derive._
import sangria.marshalling.playJson._
import sangria.schema._

import scala.collection.mutable.{Map => MutableMap}
import scala.concurrent.Future

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
    username: String,
    exercise: Exercise[EC],
    topicsWithLevels: Seq[TopicWithLevel]
  ): Future[Boolean] = Future
    .sequence {
      topicsWithLevels.map { topicWithLevel =>
        Future.successful(false)
      /*
        TODO:
        mongoQueries
          .updateUserProficiency(username, exercise, topicWithLevel)
          .recover { _ => false }
       */
      }
    }
    .map { updateResults => updateResults.forall(identity) }

  // FIXME: tools without parts!
  private val exerciseCorrectionFields: Seq[Field[GraphQLContext, Unit]] = ToolList.tools.map[Field[GraphQLContext, Unit]] {
    case toolWithParts: ToolWithParts =>
      val partTypeInputArg: Argument[toolWithParts.PartType] = {
        implicit val partFormat: Format[toolWithParts.PartType] = toolWithParts.jsonFormats.partTypeFormat

        Argument("part", toolWithParts.graphQlModels.partEnumType)
      }

      val solTypeInputArg: Argument[toolWithParts.SolInputType] = {
        implicit val solTypeFormat: Format[toolWithParts.SolInputType] = toolWithParts.jsonFormats.solutionInputFormat

        Argument("solution", toolWithParts.graphQlModels.solutionInputType)
      }

      val correctionResultType: ObjectType[Unit, CorrectionResult[toolWithParts.ResType]] = deriveObjectType(
        ObjectTypeName(s"${toolWithParts.id.capitalize}CorrectionResult"),
        ReplaceField("result", Field("result", toolWithParts.graphQlModels.resultType, resolve = _.value.result))
      )

      def correct(
        tableDefs: TableDefs,
        user: User,
        ex: Exercise[toolWithParts.ExContType],
        part: toolWithParts.PartType,
        solution: toolWithParts.SolInputType
      ): Future[CorrectionResult[toolWithParts.ResType]] = for {
        result <- toolWithParts.correctAbstract(user, solution, ex, part)

        solutionId <- tableDefs.futureInsertSolution(
          ex.toolId,
          ex.collectionId,
          ex.exerciseId,
          user.username,
          solution,
          part,
          toolWithParts.jsonFormats.solutionInputFormat,
          result.points,
          result.maxPoints
        )

        topicsForLevels <- tableDefs.futureTopicsForExercise(ex.toolId, ex.collectionId, ex.exerciseId)

        proficienciesUpdated <-
          if (result.isCompletelyCorrect) updateAllUserProficiencies(user.username, ex, topicsForLevels).map(Some.apply)
          else Future.successful(None)

      } yield CorrectionResult(result, solutionId, proficienciesUpdated)

      val toolExerciseMutationsType = ObjectType(
        s"${toolWithParts.id.capitalize}ExerciseMutations",
        fields[GraphQLContext, Exercise[toolWithParts.ExContType]](
          Field(
            "correct",
            correctionResultType,
            arguments = partTypeInputArg :: solTypeInputArg :: Nil,
            resolve = context =>
              context.ctx.loggedInUser match {
                case None => Future.failed(new Exception("User is not logged in!"))
                case Some(loggedInUser) =>
                  correct(
                    context.ctx.tableDefs,
                    loggedInUser,
                    context.value,
                    context.arg(partTypeInputArg),
                    context.arg(solTypeInputArg)
                  )
              }
          )
        )
      )

      Field(
        s"${toolWithParts.id}Exercise",
        OptionType(toolExerciseMutationsType),
        arguments = collIdArgument :: exIdArgument :: Nil,
        resolve = context => context.ctx.tableDefs.futureExerciseById(toolWithParts, context.arg(collIdArgument), context.arg(exIdArgument))
      )

    case toolWithoutParts: ToolWithoutParts =>
      val solTypeInputArg: Argument[toolWithoutParts.SolInputType] = {
        implicit val solTypeFormat: Format[toolWithoutParts.SolInputType] = toolWithoutParts.jsonFormats.solutionInputFormat

        Argument("solution", toolWithoutParts.graphQlModels.solutionInputType)
      }

      val correctionResultType: ObjectType[Unit, CorrectionResult[toolWithoutParts.ResType]] = deriveObjectType(
        ObjectTypeName(s"${toolWithoutParts.id.capitalize}CorrectionResult"),
        ReplaceField("result", Field("result", toolWithoutParts.graphQlModels.resultType, resolve = _.value.result))
      )

      def correct(
        tableDefs: TableDefs,
        user: User,
        ex: Exercise[toolWithoutParts.ExContType],
        solution: toolWithoutParts.SolInputType
      ): Future[CorrectionResult[toolWithoutParts.ResType]] = for {
        result <- toolWithoutParts.correctAbstract(user, solution, ex)

        solutionId <- tableDefs.futureInsertSolution(
          ex.toolId,
          ex.collectionId,
          ex.exerciseId,
          user.username,
          solution,
          null,
          toolWithoutParts.jsonFormats.solutionInputFormat,
          result.points,
          result.maxPoints
        )

        topicsForLevels <- tableDefs.futureTopicsForExercise(ex.toolId, ex.collectionId, ex.exerciseId)

        proficienciesUpdated <-
          if (result.isCompletelyCorrect) updateAllUserProficiencies(user.username, ex, topicsForLevels).map(Some.apply)
          else Future.successful(None)

      } yield CorrectionResult(result, solutionId, proficienciesUpdated)

      val toolExerciseMutationsType = ObjectType(
        s"${toolWithoutParts.id.capitalize}ExerciseMutations",
        fields[GraphQLContext, Exercise[toolWithoutParts.ExContType]](
          Field(
            "correct",
            correctionResultType,
            arguments = solTypeInputArg :: Nil,
            resolve = context =>
              context.ctx.loggedInUser match {
                case None => Future.failed(new Exception("User is not logged in!"))
                case Some(loggedInUser) =>
                  correct(
                    context.ctx.tableDefs,
                    loggedInUser,
                    context.value,
                    context.arg(solTypeInputArg)
                  )
              }
          )
        )
      )

      Field(
        s"${toolWithoutParts.id}Exercise",
        OptionType(toolExerciseMutationsType),
        arguments = collIdArgument :: exIdArgument :: Nil,
        resolve = context => context.ctx.tableDefs.futureExerciseById(toolWithoutParts, context.arg(collIdArgument), context.arg(exIdArgument))
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
