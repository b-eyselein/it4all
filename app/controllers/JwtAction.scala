package controllers

import com.google.inject.Inject
import model.{JwtHelpers, TableDefs, User}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

final case class JwtRequest[A](username: Option[User], request: Request[A]) extends WrappedRequest[A](request)

class JwtAction @Inject() (
  override val parser: BodyParsers.Default,
  tableDefs: TableDefs
)(override implicit val executionContext: ExecutionContext)
    extends ActionBuilder[JwtRequest, AnyContent]
    with ActionRefiner[Request, JwtRequest]
    with JwtHelpers {

  private val headerName = "authorization"

  private val BearerRegex = raw"Bearer (.*)".r

  private def usernameFromRequest[A](request: Request[A]): Try[String] = for {
    authHeader <- request.headers.get(headerName).toRight(new Exception(s"No value sent for header $headerName")).toTry

    token <- authHeader match {
      case BearerRegex(token) => Success(token)
      case _                  => Failure(new Exception("Header has wrong format!"))
    }

    deserializedToken <- deserializeJwt(token)

    username <- deserializedToken.subject.toRight(new Exception("No username in Claims!")).toTry
  } yield username

  override protected def refine[A](request: Request[A]): Future[Either[Result, JwtRequest[A]]] = usernameFromRequest(request) match {
    case Failure(_) => Future.successful(Right(JwtRequest(None, request)))
    case Success(username) =>
      for {
        maybeUser <- tableDefs.futureUserByUsername(username)
      } yield Right(JwtRequest(maybeUser, request))
  }

}
