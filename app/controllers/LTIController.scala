package controllers

import javax.inject.Inject
import play.api.mvc._

class LTIController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def honeypot: Action[AnyContent] = Action { request =>
    request.body.asFormUrlEncoded match {
      case None                                 => BadRequest("TODO!")
      case Some(data: Map[String, Seq[String]]) =>

        data.foreach {
          case (k, v) => println(k + " ==> " + v)
        }

        Ok("TODO!")
    }
  }

}
