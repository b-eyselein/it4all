package model.core

trait ExParts[Part <: ExPart] {

  def values: Seq[Part]

}

trait ExPart {

  def urlName: String

  def partName: String

}
