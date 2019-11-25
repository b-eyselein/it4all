package model.tools.collectionTools.rose

import model.points._
import model.tools.collectionTools.ToolJsonProtocol
import model.tools.collectionTools.programming.{ProgDataType, ProgLanguage, ProgLanguages, ProgrammingToolJsonProtocol}
import play.api.libs.json.{Format, Json, Writes}

object RoseToolJsonProtocol extends ToolJsonProtocol[RoseExPart, RoseExerciseContent, String, RoseUserSolution, RoseCompleteResult] {

  override val userSolutionFormat: Format[RoseUserSolution] = {
    implicit val repf: Format[RoseExPart]   = RoseExParts.jsonFormat
    implicit val plf : Format[ProgLanguage] = ProgLanguages.jsonFormat
    implicit val pf  : Format[Points]       = ToolJsonProtocol.pointsFormat

    Json.format[RoseUserSolution]
  }

  val roseInputTypeFormat: Format[RoseInputType] = {
    implicit val pdtf: Format[ProgDataType] = ProgrammingToolJsonProtocol.progDataTypeFormat

    Json.format[RoseInputType]
  }

  override val exerciseContentFormat: Format[RoseExerciseContent] = {
    implicit val ritf: Format[RoseInputType]      = roseInputTypeFormat
    implicit val rssf: Format[RoseSampleSolution] = {
      implicit val plf: Format[ProgLanguage] = ProgLanguages.jsonFormat

      Json.format[RoseSampleSolution]
    }


    Json.format[RoseExerciseContent]
  }

  // Other

  val roseExecutionResultFormat: Format[RoseExecutionResult] = {
    implicit val rsf: Format[RoseStart]   = Json.format[RoseStart]
    implicit val rrf: Format[RobotResult] = Json.format[RobotResult]

    Json.format[RoseExecutionResult]
  }

  override val completeResultWrites: Writes[RoseCompleteResult] = {
    implicit val pointsWrites: Writes[Points]              = pointsJsonWrites
    implicit val rerw        : Writes[RoseExecutionResult] = roseExecutionResultFormat

    Json.writes[RoseCompleteResult]
  }

}


