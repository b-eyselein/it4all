package model.tools.collectionTools

import enumeratum.{EnumEntry, PlayEnum}

trait ExPart extends EnumEntry {

  def urlName: String

  def partName: String

}

trait ExParts[EP <: ExPart] extends PlayEnum[EP]
