package model.xml

import model._
import model.xml.dtd.DocTypeDef
import play.twirl.api.Html


final case class XmlCompleteExercise(ex: XmlExercise, sampleGrammars: Seq[XmlSampleGrammar], sampleDocuments: Seq[XmlSampleDocument])
  extends SingleCompleteEx[XmlExercise, XmlExPart] {

  override def hasPart(partType: XmlExPart): Boolean = true

  override def preview: Html = //FIXME: move to toolMain!
    views.html.idExercises.xml.xmlPreview(this)

  def getTemplate(part: XmlExPart): String = part match {
    case XmlExParts.DocumentCreationXmlPart => s"""<?xml version="1.0" encoding="UTF-8"?>
                                                  |<!DOCTYPE ${ex.rootNode} SYSTEM "${ex.rootNode}.dtd">""".stripMargin
    case XmlExParts.GrammarCreationXmlPart  => s"<!ELEMENT ${ex.rootNode} (EMPTY)>"
  }

}


final case class XmlExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                             grammarDescription: String, rootNode: String) extends Exercise


trait XmlSample {
  val id        : Int
  val exerciseId: Int
  val exSemVer  : SemanticVersion
}


final case class XmlSampleGrammar(id: Int, exerciseId: Int, exSemVer: SemanticVersion, sampleGrammar: DocTypeDef) extends XmlSample

final case class XmlSampleDocument(id: Int, exerciseId: Int, exSemVer: SemanticVersion, document: String) extends XmlSample

final case class XmlSolution(username: String, exerciseId: Int, exSemVer: SemanticVersion, part: XmlExPart, solution: String,
                             points: Points, maxPoints: Points) extends DBPartSolution[XmlExPart, String]

final case class XmlExerciseReview(username: String, exerciseId: Int, exerciseSemVer: SemanticVersion, exercisePart: XmlExPart,
                                   difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview[XmlExPart]