package model.xml

import de.uniwue.dtd.model.DocTypeDef
import de.uniwue.dtd.parser.DocTypeDefParser
import model._
import play.twirl.api.Html


final case class XmlExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                             grammarDescription: String, rootNode: String, samples: Seq[XmlSample]) extends Exercise {

  override def baseValues: BaseValues = BaseValues(id, semanticVersion, title, author, text, state)

  // other methods

  override def preview: Html = //FIXME: move to toolMain!
    views.html.idExercises.xml.xmlPreview(this)

  def getTemplate(part: XmlExPart): String = part match {
    case XmlExParts.DocumentCreationXmlPart => s"""<?xml version="1.0" encoding="UTF-8"?>
                                                  |<!DOCTYPE ${rootNode} SYSTEM "${rootNode}.dtd">""".stripMargin
    case XmlExParts.GrammarCreationXmlPart  => s"<!ELEMENT ${rootNode} (EMPTY)>"
  }

}

final case class XmlSample(id: Int, exerciseId: Int, exSemVer: SemanticVersion, sampleGrammarString: String, sampleDocument: String)
  extends SampleSolution[String] {

  def sampleGrammar: DocTypeDef = DocTypeDefParser.tryParseDTD(sampleGrammarString).getOrElse(???)

  def bothSamples: (DocTypeDef, String) = (sampleGrammar, sampleDocument)

  override def sample = ""

}

final case class XmlSolution(id: Int, username: String, exerciseId: Int, exSemVer: SemanticVersion, part: XmlExPart,
                             solution: String, points: Points, maxPoints: Points) extends UserSolution[XmlExPart, String]

final case class XmlExerciseReview(username: String, exerciseId: Int, exerciseSemVer: SemanticVersion, exercisePart: XmlExPart,
                                   difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview[XmlExPart]
