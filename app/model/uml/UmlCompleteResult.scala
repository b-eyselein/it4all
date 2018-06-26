package model.uml

import model.core.matching.{GenericAnalysisResult, Match, MatchingResult}
import model.core.result.EvaluationResult._
import model.core.result.{CompleteResult, EvaluationResult, SuccessType}
import model.uml.UmlCompleteResult._
import model.uml.matcher._
import play.api.libs.json._

import scala.language.postfixOps

object UmlCompleteResult {

  def describeImplementation(impl: UmlImplementation): String = s"${impl.subClass}  &rarr;  ${impl.superClass}"

  def describeAssociation(assoc: UmlAssociation): String =
    s"${assoc.assocType.german}: ${assoc.firstEnd} &harr; ${assoc.secondEnd} (${assoc.displayMult(turn = false)})"

}

case class UmlCompleteResult(exercise: UmlCompleteEx, learnerSolution: UmlClassDiagram, solutionSaved: Boolean, part: UmlExPart)
  extends CompleteResult[EvaluationResult] {

  override type SolType = UmlClassDiagram

  override val success: SuccessType = SuccessType.NONE

  override def results: Seq[MatchingResult[_, _, _ <: Match[_, _]]] = Seq.empty ++ classResult ++ assocAndImplResult.map(_._1) ++ assocAndImplResult.map(_._2)

  private val musterSolution: UmlClassDiagram = exercise.ex.solution

  val classResult: Option[MatchingResult[UmlClass, UmlClassMatchAnalysisResult, UmlClassMatch]] = part match {
    case UmlExParts.DiagramDrawingHelp => None
    case UmlExParts.ClassSelection => Some(UmlClassMatcher(false).doMatch(learnerSolution.classes, musterSolution.classes))
    case UmlExParts.DiagramDrawing | UmlExParts.MemberAllocation => Some(UmlClassMatcher(true).doMatch(learnerSolution.classes, musterSolution.classes))
  }

  val assocAndImplResult: Option[(MatchingResult[UmlAssociation, UmlAssociationAnalysisResult, UmlAssociationMatch],
    MatchingResult[UmlImplementation, GenericAnalysisResult, UmlImplementationMatch])] = part match {
    case UmlExParts.DiagramDrawingHelp | UmlExParts.DiagramDrawing =>
      val assocRes = UmlAssociationMatcher.doMatch(learnerSolution.associations, musterSolution.associations)
      val implRes = UmlImplementationMatcher.doMatch(learnerSolution.implementations, musterSolution.implementations)
      Some((assocRes, implRes))
    case _ => None
  }

  def nextPart: Option[UmlExPart] = part match {
    case UmlExParts.DiagramDrawing => None

    case UmlExParts.ClassSelection => Some(UmlExParts.DiagramDrawingHelp)
    case UmlExParts.DiagramDrawingHelp => Some(UmlExParts.MemberAllocation)
    case UmlExParts.MemberAllocation => None
  }

  private def displayAssocsAndImpls: String =
    s"""<h4>Ihre Vererbungsbeziehungen:</h4>
       |<ul>
       |  ${learnerSolution.implementations map (describeImplementation(_) asListElem) mkString}
       |</ul>
       |<h4>Ihre Assoziationen:</h4>
       |<ul>
       |  ${learnerSolution.associations map (describeAssociation(_) asListElem) mkString}
       |</ul>""".stripMargin

  private def displayClasses: String = "<h4>Ihre Klassen:</h4>" + (learnerSolution.classes map { clazz =>
    s"""<p>${clazz.className}</p>
       |<ul>
       |  ${displayMembers(clazz.allMembers.map(m => m.memberName + ": " + m.memberType))}
       |</ul>""".stripMargin
  } mkString)

  private def displayMembers(members: Seq[String]): String = members map (m => "<li>" + m + "</li>") match {
    case Nil => "<li>--</li>"
    case ms => ms mkString
  }

  def toJson: JsValue = Json.obj(
    "classResult" -> classResult.map(_.toJson),
    "assocAndImplResult" -> assocAndImplResult.map {
      case (assocRes, implRes) => Json.obj(
        "assocResult" -> assocRes.toJson,
        "implResult" -> implRes.toJson)
    },
  )

}
