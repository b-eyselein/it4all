package model.tools.collectionTools.xml

import de.uniwue.dtd.model._
import model.core.matching.{AnalysisResult, Match, MatchType, Matcher}
import model.core.result.SuccessType
import model.points._
import model.tools.collectionTools.xml.XmlConsts._
import model.tools.collectionTools.xml.XmlGrammarCompleteResult.{pointsForAttribute, pointsForElement}
import play.api.libs.json.{JsValue, Json}

final case class ElementLineAnalysisResult(matchType: MatchType,
                                           contentCorrect: Boolean, correctContent: String,
                                           attributesCorrect: Boolean, correctAttributes: String) extends AnalysisResult {


  override def toJson: JsValue = XmlToolJsonProtocol.elementLineAnalysisResultWrites.writes(this)

  def points: Points = addUp(Seq(contentCorrect, attributesCorrect).map {
    case false => zeroPoints
    case true  => 1 point
  })

}

final case class ElementLineMatch(userArg: Option[ElementLine], sampleArg: Option[ElementLine]) extends Match with XmlEvaluationResult {

  override type T = ElementLine

  override type AR = ElementLineAnalysisResult

  override protected def analyze(arg1: ElementLine, arg2: ElementLine): ElementLineAnalysisResult = {
    val contentCorrect = arg1.elementDefinition.contentAsString == arg2.elementDefinition.contentAsString

    val arg1Def = arg1.attributeLists.headOption.map(_.asString)
    val arg2Def = arg2.attributeLists.headOption.map(_.asString)
    val attributesCorrect = arg1Def == arg2Def

    val matchType: MatchType = if (contentCorrect) {
      if (attributesCorrect) MatchType.SUCCESSFUL_MATCH
      else MatchType.PARTIAL_MATCH
    } else MatchType.UNSUCCESSFUL_MATCH

    ElementLineAnalysisResult(matchType,
      contentCorrect, arg2.elementDefinition.contentAsString,
      attributesCorrect, arg2Def.getOrElse("FEHLER!"))
  }

  override protected def descArgForJson(arg: ElementLine): JsValue = Json.obj(nameName -> arg.elementName)

  override def success: SuccessType = if (analysisResult.exists(_.matchType == MatchType.SUCCESSFUL_MATCH)) {
    SuccessType.COMPLETE
  } else SuccessType.NONE


  override def maxPoints: Points = sampleArg match {
    case None     => zeroPoints
    case Some(sa) => pointsForElement + pointsForElementLine(sa)
  }

  override def points: Points = matchType match {
    case MatchType.SUCCESSFUL_MATCH                             => maxPoints
    case MatchType.ONLY_SAMPLE | MatchType.ONLY_USER            => zeroPoints
    case MatchType.UNSUCCESSFUL_MATCH | MatchType.PARTIAL_MATCH =>
      // FIXME: calculate...

      val pointsForContent = analysisResult match {
        case None     => zeroPoints
        case Some(ar) =>
          val pointsForElemContent = if (ar.contentCorrect) singlePoint else zeroPoints
          val pointsForAttributes = if (ar.attributesCorrect) singlePoint else zeroPoints
          pointsForElemContent + pointsForAttributes
      }

      pointsForElement + pointsForContent
  }

  private def pointsForElementLine(elementLine: ElementLine): Points = {
    val pointsForElemContent: Points = pointsForElementContent(elementLine.elementDefinition.content)
    val pointsForAttrs: Points = addUp(elementLine.attributeLists.map(pointsForAttributes))

    pointsForElemContent + pointsForAttrs
  }

  private def pointsForElementContent(elementContent: ElementContent): Points = elementContent match {
    case _: StaticElementContent        => pointsForElement
    case _: ChildElementContent         => pointsForElement
    case u: UnaryOperatorElementContent => pointsForElement + pointsForElementContent(u.childContent)
    case m: MultiElementContent         => addUp(m.children map pointsForElementContent)
  }

  private def pointsForAttributes(attributeList: AttributeList): Points = pointsForAttribute * attributeList.attributeDefinitions.size


  //  val maxPoints = {
  //    val pointsForElements: Points = XmlGrammarCompleteResult.pointsForElement * sampleGrammar.asElementLines.size
  //
  //    val pointsForContents: Points = addUp(sampleGrammar.asElementLines.map(XmlGrammarCompleteResult.pointsForElementLine))
  //
  //    pointsForElements + pointsForContents
  //  }

}

object DocTypeDefMatcher extends Matcher[ElementLineMatch] {

  override type T = ElementLine

  override protected val matchName: String = "DTD-Zeilen"

  override protected val matchSingularName: String = "DTD-Zeile"

  override protected def canMatch(el1: ElementLine, el2: ElementLine): Boolean = el1.elementName == el2.elementName

  override protected def matchInstantiation(ua: Option[ElementLine], sa: Option[ElementLine]): ElementLineMatch =
    ElementLineMatch(ua, sa)

}
