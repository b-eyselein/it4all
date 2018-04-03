package model.uml

import model.Consts

import scala.util.matching.Regex

object UmlConsts extends Consts {

  // Numbers

  val OFFSET        = 100
  val GapHorizontal = 250
  val GapVertival   = 200

  // Text parsing

  val CapWordsRegex: Regex = """[A-Z][a-zäöüß]*""".r

  val CssClassName = "non-marked"

  val ClassSelectionFunction = "onclick=\"select(this)\""
  val DiagramDrawingFunction = "draggable=\"true\" ondragstart=\"drag(event)\""

  // Strings

  val associationsName    = "associations"
  val associationNameName = "assocName"
  val associationTypeName = "assocType"

  val classesName   = "classes"
  val classTypeName = "classType"

  val firstEndName  = "firstEnd"
  val firstMultName = "firstMult"

  val implementationsName = "implementations"
  val ignoreWordsName     = "ignoreWords"

  val mappingsName = "mappings"
  val methodsName  = "methods"

  val secondEndName  = "secondEnd"
  val secondMultName = "secondMult"
  val subclassName   = "subClass"
  val superclassName = "superClass"

}
