package model.tools.collectionTools

import model.lesson.{Lesson, LessonContent, LessonQuestionsContent, LessonTextContent, Question, QuestionAnswer}
import nl.codestar.scalatsi.TypescriptType.TypescriptNamedType
import nl.codestar.scalatsi.{TSIType, TSNamedType, TSType}
import play.api.libs.json.JsValue

object BasicTSTypes extends ToolTSInterfaceTypes {

  import nl.codestar.scalatsi.TypescriptType.TSInterface

  private val lessonTextContentTSI: TSIType[LessonTextContent] = TSType.fromCaseClass

  private val lessonQuestionsContentTSI: TSIType[LessonQuestionsContent] = {
    implicit val questionAnswerTSI: TSIType[QuestionAnswer] = TSType.fromCaseClass
    implicit val questionTSI      : TSIType[Question]       = TSType.fromCaseClass

    TSType.fromCaseClass
  }

  private val lessonTSI: TSIType[Lesson] = {
    implicit val lessonContentTSI: TSNamedType[LessonContent] = {
      implicit val tlct: TSIType[LessonTextContent]      = lessonTextContentTSI
      implicit val lqct: TSIType[LessonQuestionsContent] = lessonQuestionsContentTSI

      TSType.fromSealed
    }

    TSType.fromCaseClass
  }

  private val semanticVersionTSI: TSIType[SemanticVersion] = TSType.fromCaseClass
  private val exTagTSI          : TSIType[ExTag]           = TSType.fromCaseClass

  private val exerciseMetaDataTSI: TSIType[ExerciseMetaData] = {
    implicit val svt: TSIType[SemanticVersion] = semanticVersionTSI
    implicit val ett: TSIType[ExTag]           = exTagTSI

    TSType.fromCaseClass
  }

  private val exerciseTSI: TSIType[Exercise] = {
    implicit val svt : TSIType[SemanticVersion] = semanticVersionTSI
    implicit val ett : TSIType[ExTag]           = exTagTSI
    implicit val jvtt: TSType[JsValue]          = jsValueTsType

    TSType.fromCaseClass[Exercise]
  }

  private val exerciseCollectionTSI: TSIType[ExerciseCollection] = TSType.fromCaseClass[ExerciseCollection]


  val exported: Seq[TypescriptNamedType] = Seq(
    lessonTSI.get,
    lessonTextContentTSI.get,
    lessonQuestionsContentTSI.get,

    exerciseTSI.get,
    exerciseMetaDataTSI.get,
    exerciseCollectionTSI.get
  )

}
