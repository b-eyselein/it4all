package model.graphql

import com.github.t3hnar.bcrypt._
import controllers.JwtHelpers
import model._
import model.json.JsonProtocols
import model.tools.ToolList
import play.api.libs.json._
import play.modules.reactivemongo.MongoController
import sangria.marshalling.playJson._
import sangria.schema._

import scala.concurrent.Future
import scala.util.Try

trait GraphQLMutations extends CollectionGraphQLModel with GraphQLArguments with MongoClientQueries with JwtHelpers {
  self: MongoController =>

  private val correctionFields = ToolList.tools.map[Field[Unit, Unit]] { tool =>
    implicit val solTypeFormat: Format[tool.SolType] = tool.jsonFormats.solutionFormat
    implicit val partFormat: Format[tool.PartType]   = tool.jsonFormats.partTypeFormat

    val SolTypeInputArg  = Argument("solution", tool.graphQlModels.SolTypeInputType)
    val PartTypeInputArg = Argument("part", tool.graphQlModels.partEnumType)

    Field(
      s"correct${tool.id.capitalize}",
      tool.graphQlModels.toolAbstractResultTypeInterfaceType,
      arguments = userJwtArgument :: collIdArgument :: exIdArgument :: PartTypeInputArg :: SolTypeInputArg :: Nil,
      resolve = ctx => {

        val part     = ctx.arg(PartTypeInputArg)
        val solution = ctx.arg(SolTypeInputArg)

        deserializeJwt(ctx.arg(userJwtArgument)) match {
          case None => ??? // Future.successful(None)
          case Some(user) =>
            getExercise(tool.id, ctx.arg(collIdArgument), ctx.arg(exIdArgument), tool.jsonFormats.exerciseFormat)
              .flatMap {
                case Some(exercise) =>
                  for {
                    nextUserSolutionId <- nextUserSolutionId(exercise, part)
                    solutionSaved <- insertSolution(
                      UserSolution.forExercise(nextUserSolutionId, exercise, user.username, solution, part),
                      tool.jsonFormats.userSolutionFormat
                    )
                    result <- tool.correctAbstract(user, solution, exercise, part, solutionSaved)
                  } yield result

                case _ => ???
              }

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
        maybeUser <- if (pwOkay) {
          val loggedInUser = LoggedInUser(user.username, user.isAdmin)

          Some(LoggedInUserWithToken(loggedInUser, createJwtSession(loggedInUser).serialize))
        } else None

      } yield maybeUser
    }

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
        "insertCollection",
        BooleanType,
        arguments = toolIdArgument :: contentArgument :: Nil,
        resolve = context =>
          Try(Json.parse(context.arg(contentArgument))).fold(
            _ => Future.successful(false),
            jsonCollection =>
              JsonProtocols.collectionFormat.reads(jsonCollection) match {
                case JsError(_)               => Future.successful(false)
                case JsSuccess(collection, _) => insertCollection(collection)
              }
          )
      ),
      Field(
        "insertExercise",
        BooleanType,
        arguments = toolIdArgument :: contentArgument :: Nil,
        resolve = context => {
          ToolList.tools.find(_.id == context.arg(toolIdArgument)) match {
            case None => Future.successful(false)
            case Some(tool) =>
              val jsonExercise: JsValue = Json.parse(context.arg(contentArgument))

              tool.jsonFormats.exerciseFormat.reads(jsonExercise) match {
                case JsError(_)             => Future.successful(false)
                case JsSuccess(exercise, _) => insertExercise(exercise, tool.jsonFormats.exerciseFormat)
              }
          }
        }
      )
    ) ++ correctionFields
  )

}
