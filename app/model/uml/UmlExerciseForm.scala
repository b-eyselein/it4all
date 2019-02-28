package model.uml

import model.core.ExerciseForm
import model.uml.UmlConsts._
import model.{ExerciseState, SemanticVersionHelper}
import play.api.data.Forms._
import play.api.data.{Form, Mapping}

import scala.language.postfixOps

object UmlExerciseForm extends ExerciseForm[UmlExercise, UmlCollection] {

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

  override val collectionFormat: Form[UmlCollection] = Form(
    mapping(
      idName -> number,
      titleName -> nonEmptyText,
      authorName -> nonEmptyText,
      textName -> nonEmptyText,
      statusName -> ExerciseState.formField,
      shortNameName -> nonEmptyText
    )(UmlCollection.apply)(UmlCollection.unapply)
  )

}
