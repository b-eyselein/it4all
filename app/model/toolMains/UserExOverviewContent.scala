package model.toolMains

import model.{ExPart, SemanticVersion}
import play.api.mvc.Call

final case class UserExOverviewContent(numOfExes: Int, exesAndRoutes: Seq[ExAndRoute])


final case class ExAndRoute( exId: Int, exTitle: String, versions: Seq[SemanticVersion], routes: Seq[CallForExPart]) {

//  val exTitle: String = ex.ex.title
//
//  val exId: Int = ex.ex.id

}


final case class CallForExPart(exPart: ExPart, call: Call, enabled: Boolean)
