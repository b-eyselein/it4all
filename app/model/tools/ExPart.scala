package model.tools

import enumeratum.{EnumEntry, PlayEnum}

trait ExPart extends EnumEntry {

  def id: String

  def partName: String

}

trait ExParts[EP <: ExPart] extends PlayEnum[EP]
