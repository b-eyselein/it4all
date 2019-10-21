package model.tools.uml

import model.core.ToolForms
import model.tools.uml.UmlConsts._
import model.{Difficulties, ExerciseState, SemanticVersionHelper}
import play.api.data.Forms._
import play.api.data.{Form, Mapping}

object UmlToolForms extends ToolForms[UmlExercise, UmlExerciseReview] {

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

  private val positionMapping: Mapping[MyPosition] = mapping(
    xCoordName -> number,
    yCoordName -> number
  )(MyPosition.apply)(MyPosition.unapply)

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

  val classDiagramMapping: Mapping[UmlClassDiagram] = mapping(
    classesName -> seq(classMapping),
    associationsName -> seq(associationMapping),
    implementationsName -> seq(implementationMapping)
  )(UmlClassDiagram.apply)(UmlClassDiagram.unapply)

  // UmlSampleSolution

  private val sampleSolutionMapping: Mapping[UmlSampleSolution] = mapping(
    idName -> number,
    sampleName -> classDiagramMapping
  )(UmlSampleSolution.apply)(UmlSampleSolution.unapply)

  // Complete exercise

  override val exerciseFormat: Form[UmlExercise] = Form(
    mapping(
      idName -> number,
      semanticVersionName -> SemanticVersionHelper.semanticVersionForm.mapping,
      titleName -> nonEmptyText,
      authorName -> nonEmptyText,
      textName -> nonEmptyText,
      statusName -> ExerciseState.formField,
      "markedText" -> text,
      ignoreWordsName -> seq(text),
      mappingsName -> seq(
        tuple(keyName -> text, valueName -> text)
      ).transform[Map[String, String]](_.toMap, _.toSeq),
      sampleSolutionName -> seq(sampleSolutionMapping)
    )(UmlExercise.apply)(UmlExercise.unapply)
  )

  override val exerciseReviewForm: Form[UmlExerciseReview] = Form(
    mapping(
      difficultyName -> Difficulties.formField,
      durationName -> optional(number(min = 0, max = 100))
    )(UmlExerciseReview.apply)(UmlExerciseReview.unapply)
  )

}
