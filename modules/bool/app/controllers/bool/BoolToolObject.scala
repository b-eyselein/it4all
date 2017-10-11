package controllers.bool

import model.tools.{RandomExToolObject, ToolState}
import play.api.mvc.Call

object BoolToolObject extends RandomExToolObject("bool", "Boolesche Algebra", ToolState.LIVE) {

  // User

  override def indexCall: Call = controllers.bool.routes.BoolController.index()

  //  override def exerciseRoute(id: Int) = exercise(id)
  //
  //  def exesListRoute(id: Int) = exesListRoute(id)
  //
  //  override def correctLiveRoute(id: Int) = correctLive(id)
  //
  //  override def correctRoute(id: Int) = correct(id)

}