package model.xml

import model.xml.XmlConsts._
import model.xml.dtd.DocTypeDefParser
import model.{ExerciseState, SemanticVersion, SemanticVersionHelper}
import play.api.data.Form
import play.api.data.Forms._

import scala.language.postfixOps

object XmlCompleteExerciseForm {

  final case class XmlSampleFormValues(id: Int, grammar: String, document: String)

  def applyXmlSample(exerciseId: Int, exerciseSemVer: SemanticVersion): XmlSampleFormValues => XmlSample = {
    case XmlSampleFormValues(id, grammar, document) => XmlSample(id, exerciseId, exerciseSemVer, DocTypeDefParser.tryParseDTD(grammar).get, document)
  }

  def unapplyXmlSample(xmlSample: XmlSample): XmlSampleFormValues =
    XmlSampleFormValues(xmlSample.id, xmlSample.sampleGrammar.asString, xmlSample.sampleDocument)


  type FormData = (Int, SemanticVersion, String, String, String, ExerciseState, String, String, Seq[XmlSampleFormValues])

  val format: Form[XmlCompleteEx] = {

    def apply(exerciseId: Int, semVer: SemanticVersion, title: String, author: String, text: String, status: ExerciseState,
              grammarDescription: String, rootNode: String, samples: Seq[XmlSampleFormValues]): XmlCompleteEx =
      XmlCompleteEx(
        XmlExercise(exerciseId, semVer, title, author, text, status, grammarDescription, rootNode),
        samples map applyXmlSample(exerciseId, semVer)
      )

    def unapply(compEx: XmlCompleteEx): Option[FormData] =
      Some((compEx.ex.id, compEx.ex.semanticVersion, compEx.ex.title, compEx.ex.author, compEx.ex.text, compEx.ex.state,
        compEx.ex.grammarDescription, compEx.ex.rootNode, compEx.samples map unapplyXmlSample))


    Form(
      mapping(
        idName -> number,
        semanticVersionName -> SemanticVersionHelper.semanticVersionForm.mapping,
        titleName -> nonEmptyText,
        authorName -> nonEmptyText,
        textName -> nonEmptyText,
        statusName -> ExerciseState.formField,
        grammarDescriptionName -> nonEmptyText,
        rootNodeName -> nonEmptyText,
        samplesName -> seq(
          mapping(
            idName -> number,
            grammarName -> nonEmptyText,
            documentName -> nonEmptyText
          )(XmlSampleFormValues.apply)(XmlSampleFormValues.unapply)
        )
      )(apply)(unapply)
    )
  }

}
