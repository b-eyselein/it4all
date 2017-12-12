package controllers.randomExes

import model.Consts
import model.Enums.ToolState
import model.essentials.EssentialsConsts
import play.api.mvc.Call

case object EssentialsToolObject extends RandomExToolObject {

  override val hasTags  : Boolean   = false
  override val toolname : String    = "Allgemeiner Teil"
  override val exType   : String    = "essent"
  override val consts   : Consts    = EssentialsConsts
  override val toolState: ToolState = ToolState.LIVE

  override def indexCall: Call = controllers.randomExes.routes.EssentialsController.index()

}
