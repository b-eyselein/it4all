package model.xml

import model._
import model.xml.dtd.DocTypeDef
import play.twirl.api.Html


case class XmlCompleteExercise(ex: XmlExercise, sampleGrammars: Seq[XmlSampleGrammar]) extends PartsCompleteEx[XmlExercise, XmlExPart] {

  override def hasPart(partType: XmlExPart): Boolean = true

  override def preview: Html = //FIXME: move to toolMain!
    views.html.idExercises.xml.xmlPreview(this)

  def getTemplate(part: XmlExPart): String = part match {
    case XmlExParts.DocumentCreationXmlPart => s"""<?xml version="1.0" encoding="UTF-8"?>
                                                  |<!DOCTYPE ${ex.rootNode} SYSTEM "${ex.rootNode}.dtd">""".stripMargin
    case XmlExParts.GrammarCreationXmlPart  => s"<!ELEMENT ${ex.rootNode} (EMPTY)>"
  }

}

case class XmlExercise(id: Int, title: String, author: String, text: String, state: ExerciseState, semanticVersion: SemanticVersion,
                       grammarDescription: String, rootNode: String) extends Exercise

case class XmlSampleGrammar(id: Int, exerciseId: Int, sampleGrammar: DocTypeDef)

case class XmlSolution(username: String, exerciseId: Int, part: XmlExPart, solution: String, points: Double, maxPoints: Double) extends PartSolution[XmlExPart, String]