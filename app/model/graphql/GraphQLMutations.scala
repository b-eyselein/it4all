package model.graphql

import com.github.t3hnar.bcrypt._
import controllers.JwtHelpers
import model._
import model.json.JsonProtocols
import model.tools.{ToolList, UserSolution}
import play.api.libs.json._
import reactivemongo.api.DefaultDB
import sangria.macros.derive._
import sangria.marshalling.playJson._
import sangria.schema._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

trait GraphQLMutations extends CollectionGraphQLModel with GraphQLArguments with MongoClientQueries with JwtHelpers {

  private val registerValuesInputType: InputObjectType[RegisterValues]   = deriveInputObjectType()
  private val userCredentialsInputType: InputObjectType[UserCredentials] = deriveInputObjectType()

  private val userCredentialsArgument: Argument[UserCredentials] = {
    implicit val ucf: OFormat[UserCredentials] = JsonProtocols.userCredentialsFormat

    Argument("credentials", userCredentialsInputType)
  }

  private val registerValuesArgument: Argument[RegisterValues] = {
    implicit val rvf: OFormat[RegisterValues] = JsonProtocols.registerValuesFormat

    Argument("registerValues", registerValuesInputType)
  }

  protected implicit val ec: ExecutionContext

  private val correctionFields = ToolList.tools.map[Field[GraphQLContext, Unit]] { toolMain =>
    implicit val solTypeFormat: Format[toolMain.SolType] = toolMain.toolJsonProtocol.solutionFormat

    val SolTypeInputArg  = Argument("solution", toolMain.graphQlModels.SolTypeInputType)
    val PartTypeInputArg = Argument("part", toolMain.graphQlModels.partEnumType)

    Field(
      s"correct${toolMain.id.capitalize}",
      toolMain.graphQlModels.toolAbstractResultTypeInterfaceType,
      arguments = userJwtArgument :: collIdArgument :: exIdArgument :: PartTypeInputArg :: SolTypeInputArg :: Nil,
      resolve = context => {

        val jwtString: String = context.arg(userJwtArgument)

        deserializeJwt(jwtString) match {
          case None => ??? // Future.successful(None)
          case Some(user) =>
            val collId   = context.arg(collIdArgument)
            val exId     = context.arg(exIdArgument)
            val part     = context.arg(PartTypeInputArg)
            val solution = context.arg(SolTypeInputArg)

            getExercise(context.ctx.mongoDB, toolMain, collId, exId, toolMain.toolJsonProtocol.exerciseFormat).flatMap {
              case Some(exercise) =>
                for {
                  solutionSaved <- insertSolution(
                    context.ctx.mongoDB,
                    UserSolution(exId, collId, toolMain.id, user.username, solution),
                    toolMain.toolJsonProtocol.userSolutionFormat
                  )

                  result <- toolMain.correctAbstract(user, solution, exercise, part, solutionSaved)
                } yield result

              case _ => ???
            }

        }
      }
    )
  }

  private def register(registerValues: RegisterValues, defaultDB: Future[DefaultDB]): Future[Option[String]] =
    if (registerValues.isInvalid) {
      Future.successful(None)
    } else {
      getUser(defaultDB, registerValues.username).flatMap {
        case Some(_) => Future.successful(None)
        case None =>
          val newUser = User(registerValues.username, Some(registerValues.firstPassword.bcrypt))

          insertUser(defaultDB, newUser).map {
            case false => None
            case true  => Some(newUser.username)
          }
      }
    }

  private def authenticate(
    credentials: UserCredentials,
    defaultDB: Future[DefaultDB]
  ): Future[Option[LoggedInUserWithToken]] =
    getUser(defaultDB, credentials.username).map { maybeUser =>
      for {
        user: User      <- maybeUser
        pwHash: String  <- user.pwHash
        pwOkay: Boolean <- credentials.password.isBcryptedSafe(pwHash).toOption
        maybeUser <- if (pwOkay) {
          val loggedInUser = LoggedInUser(user.username, user.isAdmin)

          Some(LoggedInUserWithToken(loggedInUser, createJwtSession(loggedInUser).serialize))
        } else None

      } yield maybeUser
    }

  protected val MutationType: ObjectType[GraphQLContext, Unit] = ObjectType(
    "Mutation",
    fields = fields[GraphQLContext, Unit](
      Field(
        "register",
        OptionType(StringType),
        arguments = registerValuesArgument :: Nil,
        resolve = context => register(context.arg(registerValuesArgument), context.ctx.mongoDB)
      ),
      Field(
        "login",
        OptionType(loggedInUserWithTokenType),
        arguments = userCredentialsArgument :: Nil,
        resolve = context => authenticate(context.arg(userCredentialsArgument), context.ctx.mongoDB)
      ),
      Field(
        "upsertCollection",
        OptionType(CollectionType),
        arguments = toolIdArgument :: contentArgument :: Nil,
        resolve = context =>
          Try(Json.parse(context.arg(contentArgument))).fold(
            _ => Future.successful(None),
            jsonCollection =>
              JsonProtocols.collectionFormat.reads(jsonCollection) match {
                case JsError(_) => Future.successful(None)
                case JsSuccess(collection, _) =>
                  upsertExerciseCollection(context.ctx.mongoDB, collection).map {
                    case false => None
                    case true  => Some(collection)
                  }
              }
          )
      ),
      Field(
        "upsertExercise",
        OptionType(exerciseType),
        arguments = toolIdArgument :: contentArgument :: Nil,
        resolve = context => {
          ToolList.tools.find(_.id == context.arg(toolIdArgument)) match {
            case None => Future.successful(None)
            case Some(tool) =>
              val jsonExercise: JsValue = Json.parse(context.arg(contentArgument))

              tool.toolJsonProtocol.exerciseFormat.reads(jsonExercise) match {
                case JsError(_) => Future.successful(None)
                case JsSuccess(exercise, _) =>
                  insertExercise(context.ctx.mongoDB, exercise, tool.toolJsonProtocol.exerciseFormat).map {
                    case false => None
                    case true  => Some(exercise)
                  }
              }
          }
        }
      )
    ) ++ correctionFields
  )

}
