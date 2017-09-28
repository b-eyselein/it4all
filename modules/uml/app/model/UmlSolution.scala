package model

import scala.util.{Failure, Success}

import controllers.uml.UmlController
import model.exercisereading.JsonReader
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json, Reads}
import play.data.DynamicForm

case class UmlSolution(classes: List[UmlClass], associations: List[UmlAssociation], implementations: List[UmlImplementation]) {

  def allAttributes = classes.flatMap(_.attributes)

  def allMethods = classes.flatMap(_.methods)
}

object UmlSolution {

  val OFFSET = 50
  val GAP = 200

  def getClassesForDiagDrawingHelp(classes: List[UmlClass]) = {
    val sqrt = Math.round(Math.sqrt(classes.size))

    classes.zipWithIndex.map {
      case (clazz, i) ⇒ s"""
{
  name: "${clazz.name}",
  classType: "${clazz.classType.toString.toUpperCase}",
  attributes: [],
  methods: [],
  position: {
    x: ${(i / sqrt) * GAP + OFFSET},
    y: ${(i % sqrt) * GAP + OFFSET}
  }
}"""
    }.mkString(",")
  }

  implicit val umlSolutionReads: Reads[UmlSolution] = (
    (JsPath \ "classes").read[List[UmlClass]] and
    (JsPath \ "associations").read[List[UmlAssociation]] and
    (JsPath \ "implementations").read[List[UmlImplementation]])(
      UmlSolution(_, _, _))

  implicit lazy val umlClassReads: Reads[UmlClass] = (
    (JsPath \ "classType").read[String] and
    (JsPath \ "name").read[String] and
    (JsPath \ "attributes").read[List[String]] and
    (JsPath \ "methods").read[List[String]])(
      (c, n, a, m) ⇒ UmlClass(UmlClassType.fromString(c), n, a, m))

  implicit lazy val umlAssocReads: Reads[UmlAssociation] = (
    (JsPath \ "assocType").read[String] and
    (JsPath \ "start").read[UmlAssociationEnd] and
    (JsPath \ "end").read[UmlAssociationEnd])(
      (t, s, e) ⇒ UmlAssociation(UmlAssociationType.getByString(t), (s, e)))

  implicit lazy val endsReads: Reads[UmlAssociationEnd] = (
    (JsPath \ "endName").read[String] and
    (JsPath \ "multiplicity").read[String])(
      (e, m) ⇒ UmlAssociationEnd(e, Multiplicity.getByString(m)))

  implicit lazy val umlImplReads: Reads[UmlImplementation] = (
    (JsPath \ "subClass").read[String] and
    (JsPath \ "superClass").read[String])(UmlImplementation(_, _))

  def fromJson(jsonAsStr: String) = Json.parse(jsonAsStr).validate[UmlSolution] match {
    case s: JsSuccess[UmlSolution] ⇒ Some(s.get)
    case e: JsError                ⇒ println(Json.prettyPrint(JsError.toJson(e))); None
  }

  def readFromForm(form: DynamicForm) = {
    val sentJson = form.get(StringConsts.FORM_VALUE)

    JsonReader.validateJson(play.libs.Json.parse(sentJson), UmlController.SolutionSchemaNode) match {
      case Success(_) ⇒ fromJson(sentJson)
      case Failure(e) ⇒ None
    }
  }

}
