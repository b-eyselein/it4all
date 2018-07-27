package model.toolMains

import model.{CompleteEx, ExPart, Exercise}
import play.api.mvc.Call

case class UserExOverviewContent(numOfExes: Int, exesAndRoutes: Seq[ExAndRoute])

case class CallForExPart(exPart: ExPart, call: Call, enabled: Boolean)

case class ExAndRoute(ex: CompleteEx[_ <: Exercise], routes: Seq[CallForExPart])
