package model.toolMains

import model.{CompleteEx, ExPart, Exercise}
import play.api.mvc.Call

final case class UserExOverviewContent(numOfExes: Int, exesAndRoutes: Seq[ExAndRoute])

final case class CallForExPart(exPart: ExPart, call: Call, enabled: Boolean)

final case class ExAndRoute(ex: CompleteEx[_ <: Exercise], routes: Seq[CallForExPart])
