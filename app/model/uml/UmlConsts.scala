package model.uml

import model.Consts

import scala.util.matching.Regex

object UmlConsts extends Consts {

  // Numbers

  val OFFSET = 50
  val GAP    = 200


  // Text parsing

  val CapWordsRegex: Regex = """[A-Z][a-zäöüß]*""".r

  val CssClassName = "non-marked"

  val ClassSelectionFunction = "onclick=\"select(this)\""
  val DiagramDrawingFunction = "draggable=\"true\" ondragstart=\"drag(event)\""

  // Strings

  val ASSOCS_NAME    = "associations"
  val ASSOCTYPE_NAME = "assocType"

  val CLASSES_NAME   = "classes"
  val CLASSTYPE_NAME = "classType"

  val FIRST_END_NAME  = "firstEnd"
  val FIRST_MULT_NAME = "firstMult"

  val IMPLS_NAME        = "implementations"
  val IGNORE_WORDS_NAME = "ignoreWords"

  val MAPPINGS_NAME = "mappings"
  val METHODS_NAME  = "methods"

  val RETURNS_NAME = "returns"

  val SECOND_END_NAME  = "secondEnd"
  val SECOND_MULT_NAME = "secondMult"
  val SOLUTION_NAME    = "solution"
  val SUBCLASS_NAME    = "subClass"
  val SUPERCLASS_NAME  = "superClass"

  val TYPE_NAME = "type"
}
