package controllers

import javax.inject.Inject

import play.api.mvc._

class LTIController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def honeypot: Action[AnyContent] = Action { request =>
    println(request.body)
    BadRequest("TODO!")
  }

}
