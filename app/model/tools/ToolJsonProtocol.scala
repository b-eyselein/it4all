package model.tools

import model.adaption.{Proficiencies, ToolProficiency, TopicProficiency}
import model.core.matching.{Match, MatchingResult}
import model.lesson._
import model.points.Points
import play.api.libs.json._

object ToolJsonProtocol {

  val proficienciesFormat: Format[Proficiencies] = {
    implicit val toolProficiencyFormat: Format[ToolProficiency]   = Json.format
    implicit val topicProficiencyFormat: Format[TopicProficiency] = Json.format

    Json.format
  }

  val semanticVersionFormat: Format[SemanticVersion] = Json.format

  val collectionFormat: Format[ExerciseCollection] = Json.format

  val lessonContentFormat: Format[LessonContent] = {

    implicit val cfg: JsonConfiguration = JsonConfiguration(
      typeNaming = JsonNaming { fullName =>
        // FIXME: match complete string!?!
        fullName.split("\\.").lastOption match {
          case Some("LessonTextContent")      => "Text"
          case Some("LessonQuestionsContent") => "Questions"
          case _                              => fullName
        }
      }
    )

    implicit val ltcf: Format[LessonTextContent] = Json.format
    implicit val lqcf: Format[LessonQuestionsContent] = {
      implicit val quf: Format[QuestionAnswer] = Json.format
      implicit val qf: Format[Question]        = Json.format

      Json.format
    }

    Json.format
  }

  val exTagFormat: Format[ExTag] = Json.format

  /*
    val exerciseFormat: Format[Exercise] = {
      implicit val scf: Format[SemanticVersion] = semanticVersionFormat
      implicit val etf: Format[ExTag]           = exTagFormat

      Json.format
    }
   */

  val pointsFormat: Format[Points] = Format(
    (jsValue: JsValue) => Reads.DoubleReads.reads(jsValue).map(p => Points((p * 4).floor.toInt)),
    (points: Points) => Writes.DoubleWrites.writes(points.asDouble)
  )

  val exerciseFileFormat: Format[ExerciseFile] = Json.format

}

final case class KeyValueObject(key: String, value: String)

trait ToolJsonProtocol[EC <: Exercise, ST, PartType <: ExPart] {

  val exerciseFormat: Format[EC]

  val solutionFormat: Format[ST]

//  val completeResultWrites: Writes[CR]

  val partTypeFormat: Format[PartType]

  protected val keyValueObjectMapFormat: Format[Map[String, String]] = {

    val keyValueObjectFormat: Format[KeyValueObject] = Json.format[KeyValueObject]

    Format(
      Reads.seq(keyValueObjectFormat).map(_.map { case KeyValueObject(key, value) => (key, value) }.toMap),
      Writes.seq(keyValueObjectFormat).contramap(_.toSeq.map { case (key, value) => KeyValueObject(key, value) })
    )
  }

  protected def matchingResultWrites[T, M <: Match[T]](matchWrites: Writes[M]): Writes[MatchingResult[T, M]] = {
    implicit val pointsWrites: Writes[Points] = ToolJsonProtocol.pointsFormat
    implicit val mw: Writes[M]                = matchWrites

    Json.writes
  }

}

abstract class StringSampleSolutionToolJsonProtocol[E <: Exercise, PartType <: ExPart]
    extends ToolJsonProtocol[E, String, PartType] {

  override val solutionFormat: Format[String] = Format(Reads.StringReads, Writes.StringWrites)

  protected val sampleSolutionFormat: Format[SampleSolution[String]] = Json.format[SampleSolution[String]]

}
