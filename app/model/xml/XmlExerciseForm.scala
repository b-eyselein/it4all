package model.xml

import model.core.ExerciseForm
import model.xml.XmlConsts._
import model.{ExerciseState, SemanticVersion, SemanticVersionHelper}
import play.api.data.Forms._
import play.api.data.{Form, Mapping}

import scala.language.postfixOps

object XmlExerciseForm extends ExerciseForm[XmlExercise] {

  // Xml samples

  final case class XmlSampleFormValues(id: Int, grammar: String, document: String)

  def applyXmlSample(exerciseId: Int, exerciseSemVer: SemanticVersion): XmlSampleFormValues => XmlSample = {
    case XmlSampleFormValues(id, grammar, document) => XmlSample(id, exerciseId, exerciseSemVer, grammar, document)
  }

  def unapplyXmlSample(xmlSample: XmlSample): XmlSampleFormValues =
    XmlSampleFormValues(xmlSample.id, xmlSample.sampleGrammar.asString, xmlSample.sampleDocument)

  private val samplesMapping: Mapping[XmlSampleFormValues] = mapping(
    idName -> number,
    grammarName -> nonEmptyText,
    documentName -> nonEmptyText
  )(XmlSampleFormValues.apply)(XmlSampleFormValues.unapply)

  // Complete exercise

  override type FormData = (Int, SemanticVersion, String, String, String, ExerciseState, String, String, Seq[XmlSampleFormValues])

  def applyCompEx(exerciseId: Int, semVer: SemanticVersion, title: String, author: String, text: String, status: ExerciseState,
                  grammarDescription: String, rootNode: String, samples: Seq[XmlSampleFormValues]): XmlExercise =
    XmlExercise(
      exerciseId, semVer, title, author, text, status, grammarDescription, rootNode, samples map applyXmlSample(exerciseId, semVer)
    )

  override def unapplyCompEx(compEx: XmlExercise): Option[FormData] =
    Some((compEx.id, compEx.semanticVersion, compEx.title, compEx.author, compEx.text, compEx.state,
      compEx.grammarDescription, compEx.rootNode, compEx.samples map unapplyXmlSample))

  override val format: Form[XmlExercise] = Form(
    mapping(
      idName -> number,
      semanticVersionName -> SemanticVersionHelper.semanticVersionForm.mapping,
      titleName -> nonEmptyText,
      authorName -> nonEmptyText,
      textName -> nonEmptyText,
      statusName -> ExerciseState.formField,
      grammarDescriptionName -> nonEmptyText,
      rootNodeName -> nonEmptyText,
      samplesName -> seq(samplesMapping)
    )(applyCompEx)(unapplyCompEx)
  )

}
