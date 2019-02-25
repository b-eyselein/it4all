package model.toolMains

import enumeratum.{EnumEntry, PlayEnum}
import model.Role
import model.Role.RoleUser
import play.twirl.api.Html

import scala.collection.immutable

sealed abstract class ToolState(val german: String, val greek: String, requiredRole: Role) extends EnumEntry {

  def badge: Html = Html(s"<sup>$greek</sup>")

}

object ToolState extends PlayEnum[ToolState] {

  override val values: immutable.IndexedSeq[ToolState] = findValues

  case object LIVE extends ToolState("Verfügbare Tools", "", RoleUser) {
    override def badge: Html = new Html("")
  }

  case object ALPHA extends ToolState("Tools in Alpha-Status", "&alpha;", Role.RoleAdmin)

  case object BETA extends ToolState("Tools in Beta-Status", "&beta;", Role.RoleAdmin)

}
