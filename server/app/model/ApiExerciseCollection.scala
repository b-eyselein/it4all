package model

import play.api.libs.json.{Format, Json}

object ApiModelHelpers {

  val apiExBasicsJsonFormat: Format[ApiExerciseBasics] = {
    implicit val svf: Format[SemanticVersion] = SemanticVersionHelper.format

    Json.format[ApiExerciseBasics]
  }

}

final case class ApiExerciseBasics(
  id: Int,
  collId: Int,
  toolId: String,
  semanticVersion: SemanticVersion,
  title: String
)

