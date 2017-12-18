package model.uml

import model.JsonFormat
import model.uml.UmlConsts._
import model.uml.UmlEnums.{UmlAssociationType, UmlClassType, UmlMultiplicity}
import play.api.libs.json.JsValue

object UmlJsonProtocol extends JsonFormat {

  def readFromJson(jsValue: JsValue): Option[UmlSolution] = jsValue.asObj flatMap { jsObj =>
    val maybeClasses = jsObj.arrayField(CLASSES_NAME, readClassFromJson)
    val maybeAssociations = jsObj.arrayField(ASSOCS_NAME, readAssociationFromJson)
    val maybeImplementations = jsObj.arrayField(IMPLS_NAME, readImplementationFromJson)

    (maybeClasses zip maybeAssociations zip maybeImplementations).headOption map {
      case ((classes, associations), implementations) => UmlSolution(classes, associations, implementations)
    }
  }

  private def readClassFromJson(jsValue: JsValue): Option[UmlCompleteClass] = jsValue.asObj flatMap { jsObj =>
    val maybeClassname = jsObj.stringField(NAME_NAME)
    val classType = jsObj.stringField(CLASSTYPE_NAME) flatMap UmlClassType.byString getOrElse UmlClassType.CLASS
    val maybeAttributes = jsObj.arrayField(ATTRS_NAME, readAttributeFromJson)
    val maybeMethods = jsObj.arrayField(METHODS_NAME, readMethodFromJson)

    (maybeClassname zip maybeAttributes zip maybeMethods).headOption map {
      case ((name, attributes), methods) => UmlCompleteClass(UmlClass(-1, name, classType), attributes, methods)
    }
  }

  private def readAttributeFromJson(jsValue: JsValue): Option[UmlClassAttribute] = jsValue.asObj flatMap { jsObj =>
    val maybeName = jsObj.stringField(NAME_NAME)
    val maybeType = jsObj.stringField(TYPE_NAME)

    (maybeName zip maybeType).headOption map { case (name, attrType) => UmlClassAttribute(-1, "", name, attrType) }
  }

  private def readMemberFromJson[M <: UmlClassMember](jsValue: JsValue, inst: (Int, String, String, String) => M): Option[M] = jsValue.asObj flatMap { jsObj =>
    val maybeName = jsObj.stringField(NAME_NAME)
    val maybeReturns = jsObj.stringField(TYPE_NAME)

    (maybeName zip maybeReturns).headOption map { case (name, returns) => inst(-1, "", name, returns) }
  }

  private def readMethodFromJson(jsValue: JsValue): Option[UmlClassMethod] = readMemberFromJson(jsValue, UmlClassMethod)

  private def readAssociationFromJson(jsValue: JsValue): Option[UmlAssociation] = jsValue.asObj flatMap { jsObj =>
    val maybeAssocType = jsObj.stringField(ASSOCTYPE_NAME) flatMap UmlAssociationType.byString
    // TODO: assoc_name
    val maybeAssocName = jsObj.stringField(ASSOCNAME_NAME)
    val maybeFirstEnd = jsObj.stringField(FIRST_END_NAME)
    val maybeFirstMult = jsObj.stringField(FIRST_MULT_NAME) flatMap UmlMultiplicity.byString
    val maybeSecondEnd = jsObj.stringField(SECOND_END_NAME)
    val maybeSecondMult = jsObj.stringField(SECOND_MULT_NAME) flatMap UmlMultiplicity.byString

    (maybeAssocType zip maybeFirstEnd zip maybeFirstMult zip maybeSecondEnd zip maybeSecondMult).headOption map {
      case ((((assocType, firstEnd), firstMult), secondEnd), secondMult) => UmlAssociation(-1, assocType, maybeAssocName, firstEnd, firstMult, secondEnd, secondMult)
    }
  }

  private def readImplementationFromJson(jsValue: JsValue): Option[UmlImplementation] = jsValue.asObj flatMap { jsObj =>
    val maybeSubclass = jsObj.stringField(SUBCLASS_NAME)
    val maybeSuperClass = jsObj.stringField(SUPERCLASS_NAME)

    (maybeSubclass zip maybeSuperClass).headOption map { case (subClass, superClass) => UmlImplementation(-1, subClass, superClass) }
  }

}
