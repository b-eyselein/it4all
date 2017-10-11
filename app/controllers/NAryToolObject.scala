package controllers

import model.tools.{RandomExToolObject, ToolState}
import play.api.mvc.Call

object NAryToolObject extends RandomExToolObject("nary", "Zahlensyteme", ToolState.LIVE) {

  override def indexCall: Call = controllers.routes.NaryController.index()

}