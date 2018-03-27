package model.toolMains

import model.core.ExPart
import model.{CompleteEx, Exercise}
import play.api.mvc.Call

case class UserExOverviewContent(numOfExes: Int, exesAndRoutes: Seq[ExAndRoute]) {

  def pages: Int = numOfExes / ToolList.STEP + 1

}

case class ExAndRoute(ex: CompleteEx[_ <: Exercise], routes: Seq[(ExPart, Call)])
