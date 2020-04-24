package model.tools

import enumeratum.{EnumEntry, PlayEnum}

trait ExPart extends EnumEntry {

  val id: String

  val partName: String

  def isEntryPart: Boolean = true

}

trait ExParts[EP <: ExPart] extends PlayEnum[EP]
