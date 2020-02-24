package model.tools.collectionTools.uml

import model.tools.{ToolConsts, ToolState}

object UmlConsts extends ToolConsts {

  override val toolName: String     = "Uml - Klassendiagramme"
  override val toolId: String       = "uml"
  override val toolState: ToolState = ToolState.BETA

  val associationsName: String     = "associations"
  val associationNameName: String  = "assocName"
  val associationTypeName: String  = "assocType"
  val attributesResultName: String = "attributesResult"

  val classesName: String   = "classes"
  val classNameName: String = "className"
  val classTypeName: String = "classType"

  val firstEndName: String  = "firstEnd"
  val firstMultName: String = "firstMult"

  val implementationsName: String = "implementations"
  val ignoreWordsName: String     = "ignoreWords"

  val mappingsName: String      = "mappings"
  val methodsName: String       = "methods"
  val methodsResultName: String = "methodsResult"

  val positionName: String = "position"

  val secondEndName: String  = "secondEnd"
  val secondMultName: String = "secondMult"
  val subClassName: String   = "subClass"
  val superClassName: String = "superClass"

  val visibilityName: String = "visibility"

  val xCoordName: String = "xCoord"
  val yCoordName: String = "yCoord"

}
