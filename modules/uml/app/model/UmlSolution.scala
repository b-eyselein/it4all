package model

import java.util.Optional

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.JsError
import play.api.libs.json.JsPath
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.Reads.StringReads
import play.data.DynamicForm
import controllers.uml.UmlController
import model.exercisereading.JsonReader

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
      case (clazz, i) => s"""
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
      (c, n, a, m) => UmlClass(UmlClassType.fromString(c), n, a, m))

  implicit lazy val umlAssocReads: Reads[UmlAssociation] = (
    (JsPath \ "assocType").read[String] and
    (JsPath \ "start").read[UmlAssociationEnd] and
    (JsPath \ "end").read[UmlAssociationEnd])(
      (t, s, e) => UmlAssociation(UmlAssociationType.getByString(t), (s, e)))

  implicit lazy val endsReads: Reads[UmlAssociationEnd] = (
    (JsPath \ "endName").read[String] and
    (JsPath \ "multiplicity").read[String])(
      (e, m) => UmlAssociationEnd(e, Multiplicity.getByString(m)))

  implicit lazy val umlImplReads: Reads[UmlImplementation] = (
    (JsPath \ "subClass").read[String] and
    (JsPath \ "superClass").read[String])(UmlImplementation(_, _))

  def fromJson(jsonAsStr: String): Optional[UmlSolution] = Json.parse(jsonAsStr).validate[UmlSolution] match {
    case s: JsSuccess[UmlSolution] => Optional.of(s.get)
    case e: JsError => println(Json.prettyPrint(JsError.toJson(e))); Optional.empty()
  }

  def readFromForm(form: DynamicForm): java.util.Optional[UmlSolution] = {
    val sentJson = form.get(StringConsts.FORM_VALUE)

    val report = JsonReader.validateJson(play.libs.Json.parse(sentJson), UmlController.SOLUTION_SCHEMA_NODE).get

    if (!report.isSuccess)
      Optional.empty()

    fromJson(sentJson)
  }

}
