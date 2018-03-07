package model.uml

import model.JsonFormat
import model.uml.UmlConsts._
import model.uml.UmlEnums.{UmlAssociationType, UmlClassType, UmlMultiplicity}
import play.api.libs.json.JsValue

object UmlJsonProtocol extends JsonFormat {

  def readUmlSolutionFromJson(username: String, exerciseId: Int, part: UmlExPart, jsValue: JsValue): Option[UmlSolution] = jsValue.asObj flatMap { jsObj =>
    for {
      classes <- jsObj.arrayField(CLASSES_NAME, readClassFromJson)
      associations <- jsObj.arrayField(ASSOCS_NAME, readAssociationFromJson)
      implementations <- jsObj.arrayField(IMPLS_NAME, readImplementationFromJson)
    } yield UmlSolution(username, exerciseId, part, classes, associations, implementations)
  }

  private def readClassFromJson(jsValue: JsValue): Option[UmlCompleteClass] = jsValue.asObj flatMap { jsObj =>
    for {
      classname <- jsObj.stringField(nameName)
      classType = jsObj.stringField(CLASSTYPE_NAME) flatMap UmlClassType.byString getOrElse UmlClassType.CLASS
      attributes <- jsObj.arrayField(attrsName, readMemberFromJson(_, UmlClassAttribute))
      methods <- jsObj.arrayField(METHODS_NAME, readMemberFromJson(_, UmlClassMethod))
    } yield UmlCompleteClass(UmlClass(-1, classname, classType), attributes, methods)
  }

  private def readMemberFromJson[M <: UmlClassMember](jsValue: JsValue, inst: (Int, String, String, String) => M): Option[M] = jsValue.asObj flatMap { jsObj =>
    for {
      name <- jsObj.stringField(nameName)
      returns <- jsObj.stringField(TYPE_NAME)
    } yield inst(-1, "", name, returns)
  }

  private def readAssociationFromJson(jsValue: JsValue): Option[UmlAssociation] = jsValue.asObj flatMap { jsObj =>

    // TODO: assoc_name ?!?
    val maybeAssocName = jsObj.stringField(ASSOCNAME_NAME)

    for {
      assocType <- jsObj.stringField(ASSOCTYPE_NAME) flatMap UmlAssociationType.byString
      firstEnd <- jsObj.stringField(FIRST_END_NAME)
      firstMult <- jsObj.stringField(FIRST_MULT_NAME) flatMap UmlMultiplicity.byString
      secondEnd <- jsObj.stringField(SECOND_END_NAME)
      secondMult <- jsObj.stringField(SECOND_MULT_NAME) flatMap UmlMultiplicity.byString
    } yield UmlAssociation(-1, assocType, maybeAssocName, firstEnd, firstMult, secondEnd, secondMult)
  }

  private def readImplementationFromJson(jsValue: JsValue): Option[UmlImplementation] = jsValue.asObj flatMap { jsObj =>
    for {
      subClass <- jsObj.stringField(SUBCLASS_NAME)
      superClass <- jsObj.stringField(SUPERCLASS_NAME)
    } yield UmlImplementation(-1, subClass, superClass)
  }

}
