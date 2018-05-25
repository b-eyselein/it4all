package model.toolMains

import model.core.ExPart
import model.{CompleteEx, Exercise}
import play.api.mvc.Call

case class UserExOverviewContent(numOfExes: Int, exesAndRoutes: Seq[ExAndRoute]) {

  def pages: Int = numOfExes / ToolList.STEP + 1

}

case class CallForExPart(exPart: ExPart, call: Call, enabled: Boolean)

case class ExAndRoute(ex: CompleteEx[_ <: Exercise], routes: Seq[CallForExPart])
