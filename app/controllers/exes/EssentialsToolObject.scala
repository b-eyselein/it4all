package controllers.exes

import model.Enums.ToolState
import model.core.tools.RandomExToolObject
import play.api.mvc.Call

object EssentialsToolObject extends RandomExToolObject("essent", "Allgemeiner Teil", ToolState.LIVE) {

  override def indexCall: Call = controllers.exes.routes.EssentialsController.index()

}
