package model.tools.collectionTools

import model.core.result.SuccessType
import model.lesson.{Lesson, LessonContent, LessonQuestionsContent, LessonTextContent, Question, QuestionAnswer}
import nl.codestar.scalatsi.TypescriptType._
import nl.codestar.scalatsi.{DefaultTSTypes, TSIType, TSNamedType, TSType}
import play.api.libs.json.JsValue


trait ToolTSInterfaceTypes extends DefaultTSTypes {

  import nl.codestar.scalatsi.dsl._

  def enumTsType[E <: enumeratum.EnumEntry, P <: enumeratum.Enum[E]](companion: P): TSType[E] =
    TSType.alias(companion.getClass.getSimpleName.replace("$", ""), TSUnion(companion.values.map(_.entryName)))

  val jsValueTsType: TSType[JsValue] = TSType.sameAs[JsValue, Any](anyTSType)

  val exerciseFileTSI: TSIType[ExerciseFile] = TSType.fromCaseClass[ExerciseFile] + ("active?" -> TSBoolean.get)

  protected val successTypeTS: TSType[SuccessType] = enumTsType(SuccessType)


  protected val stringMapTsType: TSType[Map[String, String]] = TSType.alias("KeyValueObjectMap", TSArray(
    TSType.interface[KeyValueObject]("key" -> TSString, "value" -> TSString).get
  ))


  def sampleSolutionTSI[SolType](solTypeTSI: TSType[SolType])(implicit x: Manifest[SampleSolution[SolType]]): TSIType[SampleSolution[SolType]] = {
    //    implicit val eft: TSIType[ExerciseFile] = exerciseFileTSI
    //    implicit val stt: TSType[SolType]       = solTypeTSI

    TSType.interface[SampleSolution[SolType]](
      "id" -> TSNumber,
      "sample" -> TSObject // solTypeTSI.get
    )
    //    TSType.fromCaseClass[SampleSolution[SolType]]
  }

  // Collections, Exercises and ExerciseContents

  val lessonTextContentTSI: TSIType[LessonTextContent] = TSType.fromCaseClass
  implicit val lessonQuestionsContentTSI: TSIType[LessonQuestionsContent] = {
    implicit val questionAnswerTSI: TSIType[QuestionAnswer] = TSType.fromCaseClass
    implicit val questionTSI      : TSIType[Question]       = TSType.fromCaseClass

    TSType.fromCaseClass
  }

  val lessonTSI: TSIType[Lesson] = {
    implicit val lessonContent: TSNamedType[LessonContent] = TSType.fromSealed

    TSType.fromCaseClass[Lesson]
  }

  private val semanticVersionTSI: TSIType[SemanticVersion] = TSType.fromCaseClass
  private val exTagTSI          : TSIType[ExTag]           = TSType.fromCaseClass

  val exerciseMetaDataTSI: TSIType[ExerciseMetaData] = {
    implicit val svt: TSIType[SemanticVersion] = semanticVersionTSI
    implicit val ett: TSIType[ExTag]           = exTagTSI

    TSType.fromCaseClass
  }

  val exerciseTSI: TSIType[Exercise] = {
    implicit val svt : TSIType[SemanticVersion] = semanticVersionTSI
    implicit val ett : TSIType[ExTag]           = exTagTSI
    implicit val jvtt: TSType[JsValue]          = jsValueTsType

    TSType.fromCaseClass[Exercise]
  }

  val exerciseCollectionTSI: TSIType[ExerciseCollection] = TSType.fromCaseClass[ExerciseCollection]


}
