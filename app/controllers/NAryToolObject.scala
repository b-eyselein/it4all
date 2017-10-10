package controllers

import model.tools.{RandomExToolObject, ToolState}
import play.api.Configuration
import play.mvc.Call

class NAryToolObject(c: Configuration) extends RandomExToolObject(c, "nary", "Zahlensyteme", ToolState.LIVE) {

  override def indexCall: Call = controllers.routes.NaryController.index()

}