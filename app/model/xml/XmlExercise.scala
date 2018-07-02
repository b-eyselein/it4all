package model.xml

import model._
import model.xml.dtd.DocTypeDef
import play.twirl.api.Html


case class XmlCompleteExercise(ex: XmlExercise, sampleGrammars: Seq[XmlSampleGrammar]) extends PartsCompleteEx[XmlExercise, XmlExPart] {

  override def hasPart(partType: XmlExPart): Boolean = true

  override def preview: Html = //FIXME: move to toolMain!
    views.html.idExercises.xml.xmlPreview(this)

  override def textForPart(urlName: String): String = XmlExParts.values.find(_.urlName == urlName) match {
    case None                                     => text
    case Some(XmlExParts.DocumentCreationXmlPart) => "Erstellen Sie ein XML-Dokument zu folgender Grammatik:"
    case Some(XmlExParts.GrammarCreationXmlPart)  => "Erstellen Sie eine DTD zu folgender Beschreibung. Benutzen Sie die in Klammern angegebenen Element- bzw. Attributnamen."
  }

  def getTemplate(part: XmlExPart): String = part match {
    case XmlExParts.DocumentCreationXmlPart => s"""<?xml version="1.0" encoding="UTF-8"?>
                                                  |<!DOCTYPE ${ex.rootNode} SYSTEM "${ex.rootNode}.dtd">""".stripMargin
    case XmlExParts.GrammarCreationXmlPart  => s"<!ELEMENT ${ex.rootNode} (EMPTY)>"
  }

}

case class XmlExercise(id: Int, title: String, author: String, text: String, state: ExerciseState, grammarDescription: String, rootNode: String) extends Exercise

case class XmlSampleGrammar(id: Int, exerciseId: Int, sampleGrammar: DocTypeDef)

case class XmlSolution(username: String, exerciseId: Int, part: XmlExPart, solution: String, points: Double, maxPoints: Double) extends PartSolution[XmlExPart, String]