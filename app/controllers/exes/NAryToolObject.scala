package controllers.exes

import model.Enums.ToolState
import model.core.tools.RandomExToolObject
import play.api.mvc.Call

object NAryToolObject extends RandomExToolObject("nary", "Zahlensyteme", ToolState.LIVE) {

  override def indexCall: Call = controllers.exes.routes.EssentialsController.index()

}
