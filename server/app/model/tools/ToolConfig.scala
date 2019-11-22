package model.tools

import enumeratum.{EnumEntry, PlayEnum}
import model.{Consts, Role}

sealed abstract class ToolState(val german: String, val greek: String, requiredRole: Role) extends EnumEntry

object ToolState extends PlayEnum[ToolState] {

  override val values: IndexedSeq[ToolState] = findValues


  case object LIVE extends ToolState("Verfügbare Tools", "", Role.RoleUser)

  case object ALPHA extends ToolState("Tools in Alpha-Status", "&alpha;", Role.RoleAdmin)

  case object BETA extends ToolState("Tools in Beta-Status", "&beta;", Role.RoleAdmin)

}



trait ToolConsts extends Consts {

  val toolName: String
  val toolId  : String

  val toolState: ToolState

}
