package model.uml

import model.{ExerciseState, SemanticVersion, SemanticVersionHelper}
import model.core.ExerciseForm
import play.api.data.{Form, Mapping}
import play.api.data.Forms._
import model.uml.UmlConsts._

import scala.language.postfixOps

object UmlExerciseForm extends ExerciseForm[UmlExercise] {

  // UmlClassDiagram

  private val attributeMapping: Mapping[UmlAttribute] = mapping(
    visibilityName -> UmlVisibility.formField,
    nameName -> nonEmptyText,
    typeName -> nonEmptyText,
    "isStatic" -> boolean,
    "isDerived" -> boolean,
    "isAbstract" -> boolean
  )(UmlAttribute.apply)(UmlAttribute.unapply)

  private val methodMapping: Mapping[UmlMethod] = mapping(
    visibilityName -> UmlVisibility.formField,
    nameName -> nonEmptyText,
    typeName -> nonEmptyText,
    "parameters" -> nonEmptyText,
    "isStatic" -> boolean,
    "isAbstract" -> boolean
  )(UmlMethod.apply)(UmlMethod.unapply)

  private val positionMapping: Mapping[Position] = mapping(
    xCoordName -> number,
    yCoordName -> number
  )(Position.apply)(Position.unapply)

  private val classMapping = mapping(
    classTypeName -> UmlClassType.formField,
    classNameName -> nonEmptyText,
    attributesName -> seq(attributeMapping),
    methodsName -> seq(methodMapping),
    positionName -> optional(positionMapping)
  )(UmlClass.apply)(UmlClass.unapply)

  private val associationMapping = mapping(
    associationTypeName -> UmlAssociationType.formField,
    associationNameName -> optional(nonEmptyText),
    firstEndName -> nonEmptyText,
    firstMultName -> UmlMultiplicity.formField,
    secondEndName -> nonEmptyText,
    secondMultName -> UmlMultiplicity.formField
  )(UmlAssociation.apply)(UmlAssociation.unapply)

  private val implementationMapping = mapping(
    subClassName -> nonEmptyText,
    superClassName -> nonEmptyText
  )(UmlImplementation.apply)(UmlImplementation.unapply)

  private val classDiagramMapping: Mapping[UmlClassDiagram] = mapping(
    classesName -> seq(classMapping),
    associationsName -> seq(associationMapping),
    implementationsName -> seq(implementationMapping)
  )(UmlClassDiagram.apply)(UmlClassDiagram.unapply)

  // UmlSampleSolution

  final case class UmlSampleSolutionFormValues(id: Int, sample: UmlClassDiagram)

  private val sampleSolutionMapping: Mapping[UmlSampleSolutionFormValues] = mapping(
    idName -> number,
    sampleName -> classDiagramMapping
  )(UmlSampleSolutionFormValues.apply)(UmlSampleSolutionFormValues.unapply)

  private def unapplySampleSolution(sample: UmlSampleSolution): UmlSampleSolutionFormValues =
    UmlSampleSolutionFormValues(sample.id, sample.sample)

  private def applySampleSolution(exerciseId: Int, exSemVer: SemanticVersion): UmlSampleSolutionFormValues => UmlSampleSolution = {
    case UmlSampleSolutionFormValues(id, sample) => UmlSampleSolution(id, exerciseId, exSemVer, sample)
  }

  // Complete exercise

  override type FormData = (Int, SemanticVersion, String, String, String, ExerciseState, Seq[String], Seq[(String, String)], Seq[UmlSampleSolutionFormValues])

  override def unapplyCompEx(compEx: UmlExercise): Option[FormData] =
    Some(
      (compEx.id, compEx.semanticVersion, compEx.title, compEx.author, compEx.text, compEx.state,
        compEx.toIgnore, compEx.mappings toSeq, compEx.sampleSolutions map unapplySampleSolution)
    )

  def applyCompEx(id: Int, semVer: SemanticVersion, title: String, author: String, exText: String, state: ExerciseState,
                  toIgnore: Seq[String], mappings: Seq[(String, String)], sampleSolutionFormValues: Seq[UmlSampleSolutionFormValues]): UmlExercise = {
    val markedText: String = ???

    UmlExercise(
      id, semVer, title, author, exText, state, markedText, toIgnore, mappings toMap,
      sampleSolutions = sampleSolutionFormValues map applySampleSolution(id, semVer) // TODO!
    )
  }

  override val format: Form[UmlExercise] = Form(
    mapping(
      idName -> number,
      semanticVersionName -> SemanticVersionHelper.semanticVersionForm.mapping,
      titleName -> nonEmptyText,
      authorName -> nonEmptyText,
      textName -> nonEmptyText,
      statusName -> ExerciseState.formField,
      ignoreWordsName -> seq(text),
      mappingsName -> seq(
        tuple(keyName -> text, valueName -> text)
      ),
      sampleSolutionName -> seq(sampleSolutionMapping)
    )(applyCompEx)(unapplyCompEx)
  )

}
