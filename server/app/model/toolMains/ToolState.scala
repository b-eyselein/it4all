package model.toolMains

import enumeratum.{EnumEntry, PlayEnum}
import model.Role

sealed abstract class ToolState(val german: String, val greek: String, requiredRole: Role) extends EnumEntry

object ToolState extends PlayEnum[ToolState] {

  override val values: IndexedSeq[ToolState] = findValues


  case object LIVE extends ToolState("Verfügbare Tools", "", Role.RoleUser)

  case object ALPHA extends ToolState("Tools in Alpha-Status", "&alpha;", Role.RoleAdmin)

  case object BETA extends ToolState("Tools in Beta-Status", "&beta;", Role.RoleAdmin)

}
