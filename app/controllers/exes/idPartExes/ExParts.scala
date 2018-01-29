package controllers.exes.idPartExes


// FIXME: move to other package...

trait ExParts[Part <: ExPart] {

  def values: Seq[Part]

}

trait ExPart {

  def urlName: String

  def partName: String

}