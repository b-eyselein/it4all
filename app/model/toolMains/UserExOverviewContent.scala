package model.toolMains

import model.{CompleteEx, ExPart, Exercise, SemanticVersion}
import play.api.mvc.Call

final case class UserExOverviewContent(numOfExes: Int, exesAndRoutes: Seq[ExAndRoute])

final case class CallForExPart(exPart: ExPart, call: Call, enabled: Boolean)

final case class ExAndRoute(ex: CompleteEx[_ <: Exercise], versions: Seq[SemanticVersion], routes: Seq[CallForExPart])
