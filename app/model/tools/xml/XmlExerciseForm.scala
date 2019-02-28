package model.tools.xml

import model.core.ExerciseForm
import model.tools.xml.XmlConsts._
import model.{ExerciseState, SemanticVersionHelper}
import play.api.data.Forms._
import play.api.data.{Form, Mapping}

import scala.language.postfixOps

object XmlExerciseForm extends ExerciseForm[XmlExercise, XmlCollection] {

  // Xml samples

  private val samplesMapping: Mapping[XmlSampleSolution] = mapping(
    idName -> number,
    documentName -> nonEmptyText,
    grammarName -> nonEmptyText
  )((id, doc, gra) => XmlSampleSolution.apply(id, XmlSolution(doc, gra)))(sample => Some((sample.id, sample.sample.document, sample.sample.grammar)))

  // Complete exercise

  override val exerciseFormat: Form[XmlExercise] = Form(
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
    )(XmlExercise.apply)(XmlExercise.unapply)
  )

  override val collectionFormat: Form[XmlCollection] = Form(
    mapping(
      idName -> number,
      titleName -> nonEmptyText,
      authorName -> nonEmptyText,
      textName -> nonEmptyText,
      statusName -> ExerciseState.formField,
      shortNameName -> nonEmptyText
    )(XmlCollection.apply)(XmlCollection.unapply)
  )

}
