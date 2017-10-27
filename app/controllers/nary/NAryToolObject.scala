package controllers.nary

import model.Enums.ToolState
import model.core.tools.RandomExToolObject
import play.api.mvc.Call

object NAryToolObject extends RandomExToolObject("nary", "Zahlensyteme", ToolState.LIVE) {

  override def indexCall: Call = controllers.nary.routes.NaryController.index()

}
