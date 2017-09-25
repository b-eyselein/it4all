package controllers.bool

import model.tools.RandomExToolObject
import model.tools.ToolState

object BoolToolObject extends RandomExToolObject("Boolesche Algebra", ToolState.LIVE) {

  // User

  override def indexCall = controllers.bool.routes.BoolController.index()

  //  override def exerciseRoute(id: Int) = exercise(id)
  //
  //  def exesListRoute(id: Int) = exesListRoute(id)
  //
  //  override def correctLiveRoute(id: Int) = correctLive(id)
  //
  //  override def correctRoute(id: Int) = correct(id)

}