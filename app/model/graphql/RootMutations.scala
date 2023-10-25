package model.graphql

import com.github.t3hnar.bcrypt._
import model._
import model.tools.flask.FlaskTool
import model.tools.{ToolList, ToolWithParts, ToolWithoutParts}
import play.api.Logger
import play.api.libs.json._
import sangria.macros.derive._
import sangria.marshalling.playJson._
import sangria.schema._

import scala.collection.mutable.{Map => MutableMap}
import scala.concurrent.Future

final case class RegisterValues(username: String, password: String, passwordRepeat: String)

final case class UserCredentials(username: String, password: String)

trait RootMutations extends ExerciseQuery with JwtHelpers {

  private val logger = Logger(classOf[RootMutations])

  // Registration

  protected val registerValuesArgument: Argument[RegisterValues] = {
    implicit val x0: OFormat[RegisterValues]                     = Json.format
    val registerValuesInputType: InputObjectType[RegisterValues] = deriveInputObjectType()

    Argument[RegisterValues]("registerValues", registerValuesInputType)
  }

  private val resolveRegister: Resolver[Unit, String] = context => {
    val RegisterValues(username, password, passwordRepeat) = context.arg(registerValuesArgument)

    if (password != passwordRepeat) {

      Future.failed(MyUserFacingGraphQLError("Passwords don't match!"))

    } else {

      for {
        maybeUser <- context.ctx.tableDefs.futureUserByUsername(username)

        newUser <- maybeUser match {
          case Some(_) => Future.failed(MyUserFacingGraphQLError("Could not register user!"))
          case None =>
            context.ctx.tableDefs
              .futureInsertUser(username, Some(password.boundedBcrypt))
              .map { case User(username, _) => username }
        }
      } yield newUser
    }
  }

  // Login

  protected val userCredentialsArgument: Argument[UserCredentials] = {
    implicit val ucf: OFormat[UserCredentials]                     = Json.format
    val userCredentialsInputType: InputObjectType[UserCredentials] = deriveInputObjectType()

    Argument("credentials", userCredentialsInputType)
  }

  private val resolveLogin: Resolver[Unit, String] = context => {
    val UserCredentials(username, password) = context.arg(userCredentialsArgument)

    val onError = MyUserFacingGraphQLError(s"Invalid combination of username and password!")

    for {
      maybeUser <- context.ctx.tableDefs.futureUserByUsername(username)

      user <- futureFromOption(maybeUser, onError)

      pwHash <- futureFromOption(user.pwHash, onError)

      pwOkay <- Future.fromTry { password.isBcryptedSafeBounded(pwHash) }

      maybeUser <-
        if (pwOkay) {
          Future.successful(createJwtSession(user.username))
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

        _ =
          if (ex.toolId == FlaskTool.id) { logger.warn(result.toString) }
          else { () }

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

        _ =
          if (ex.toolId == FlaskTool.id) { logger.warn(result.toString) }
          else { () }
      } yield CorrectionResult(result, solutionId)

      val toolExerciseMutationsType = ObjectType(
        s"${toolWithParts.id.capitalize}ExerciseMutations",
        fields[GraphQLContext, Exercise[toolWithParts.ExContType]](
          Field(
            "correct",
            correctionResultType,
            arguments = partTypeInputArg :: solTypeInputArg :: Nil,
            resolve = context => {
              logger.warn("Resolving correction...")

              context.ctx.loggedInUser match {
                case None => Future.failed(MyUserFacingGraphQLError("User is not logged in!"))
                case Some(loggedInUser) =>
                  logger.warn("TODO: correcting...")
                  correct(
                    context.ctx.tableDefs,
                    loggedInUser,
                    context.value,
                    context.arg(partTypeInputArg),
                    context.arg(solTypeInputArg)
                  )
              }
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
                case None => Future.failed(MyUserFacingGraphQLError("User is not logged in!"))
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

  protected val jwtHashesToClaim: MutableMap[String, String] = MutableMap.empty

  protected val mutationType: ObjectType[GraphQLContext, Unit] = ObjectType(
    "Mutation",
    fields = fields[GraphQLContext, Unit](
      Field("register", OptionType(StringType), arguments = registerValuesArgument :: Nil, resolve = resolveRegister),
      Field("login", StringType, arguments = userCredentialsArgument :: Nil, resolve = resolveLogin),
      Field(
        "claimLtiWebToken",
        OptionType(StringType),
        arguments = ltiUuidArgument :: Nil,
        resolve = context => jwtHashesToClaim.remove(context.arg(ltiUuidArgument))
      )
    ) ++ exerciseCorrectionFields
  )

}
