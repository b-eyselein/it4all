package model.core

trait ExParts[Part <: ExPart] {

  def values: Seq[Part]

}

trait ExPart {

  // FIXME: use enumeratum=!=

  def urlName: String

  def partName: String

}
