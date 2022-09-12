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

trait RootMutations extends ExerciseQuery with JwtHelpers {

  // Registration

  protected val registerValuesArgument: Argument[RegisterValues] = {
    implicit val x0: OFormat[RegisterValues]                     = Json.format
    val registerValuesInputType: InputObjectType[RegisterValues] = deriveInputObjectType()

    Argument[RegisterValues]("registerValues", registerValuesInputType)
  }

  private val resolveRegister: Resolver[Unit, Option[String]] = context => {
    val registerValues = context.arg(registerValuesArgument)

    for {
      _ <-
        if (registerValues.isInvalid) {
          Future.failed(new Exception(s"Passwords don't match!"))
        } else {
          Future.successful(())
        }

      maybeUser <- context.ctx.tableDefs.futureUserByUsername(registerValues.username)

      newUser <- maybeUser match {
        case Some(_) => Future.successful(None)
        case None =>
          val RegisterValues(username, firstPassword, _) = registerValues

          context.ctx.tableDefs
            .futureInsertUser(username, Some(firstPassword.boundedBcrypt))
            .map { case User(username, _) => Some(username) }
      }
    } yield newUser
  }

  // Login

  protected val userCredentialsArgument: Argument[UserCredentials] = {
    implicit val ucf: OFormat[UserCredentials]                     = Json.format
    val userCredentialsInputType: InputObjectType[UserCredentials] = deriveInputObjectType()

    Argument("credentials", userCredentialsInputType)
  }

  private val loginResultType: ObjectType[Unit, LoginResult] = deriveObjectType()

  protected val jwtHashesToClaim: MutableMap[String, LoginResult] = MutableMap.empty

  private val resolveLogin: Resolver[Unit, LoginResult] = context => {
    val credentials = context.arg(userCredentialsArgument)

    val onError = new Exception(s"Invalid combination of username and password!")

    for {
      maybeUser <- context.ctx.tableDefs.futureUserByUsername(credentials.username)

      user <- futureFromOption(maybeUser, onError)

      pwHash <- futureFromOption(user.pwHash, onError)

      pwOkay <- Future.fromTry {
        credentials.password.isBcryptedSafeBounded(pwHash)
      }

      maybeUser <-
        if (pwOkay) {
          Future.successful(LoginResult(user.username, createJwtSession(user.username)))
        } else {
          Future.failed(onError)
        }

    } yield maybeUser
  }

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

        solutionId <- tableDefs.futureInsertSolutionWithPart(
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
      } yield CorrectionResult(result, solutionId)

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

        solutionId <- tableDefs.futureInsertSolutionWithoutPart(
          ex.toolId,
          ex.collectionId,
          ex.exerciseId,
          user.username,
          solution,
          toolWithoutParts.jsonFormats.solutionInputFormat,
          result.points,
          result.maxPoints
        )
      } yield CorrectionResult(result, solutionId)

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

  private val ltiUuidArgument: Argument[String] = Argument("ltiUuid", StringType)

  protected val mutationType: ObjectType[GraphQLContext, Unit] = ObjectType(
    "Mutation",
    fields = fields[GraphQLContext, Unit](
      Field("register", OptionType(StringType), arguments = registerValuesArgument :: Nil, resolve = resolveRegister),
      Field("login", loginResultType, arguments = userCredentialsArgument :: Nil, resolve = resolveLogin),
      Field(
        "claimLtiWebToken",
        OptionType(loginResultType),
        arguments = ltiUuidArgument :: Nil,
        resolve = context => jwtHashesToClaim.remove(context.arg(ltiUuidArgument))
      )
    ) ++ exerciseCorrectionFields
  )

}
