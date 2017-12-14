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

  val ASSOCS_NAME    = "associations"
  val ASSOCNAME_NAME = "assocName"
  val ASSOCTYPE_NAME = "assocType"

  val CLASSES_NAME   = "classes"
  val CLASSTYPE_NAME = "classType"

  val FIRST_END_NAME  = "firstEnd"
  val FIRST_MULT_NAME = "firstMult"

  val IMPLS_NAME        = "implementations"
  val IGNORE_WORDS_NAME = "ignoreWords"

  val MAPPINGS_NAME = "mappings"
  val METHODS_NAME  = "methods"

  val ReturnTypeName = "returnType"

  val SECOND_END_NAME  = "secondEnd"
  val SECOND_MULT_NAME = "secondMult"
  val SOLUTION_NAME    = "solution"
  val SUBCLASS_NAME    = "subClass"
  val SUPERCLASS_NAME  = "superClass"

  val TYPE_NAME = "type"
}
