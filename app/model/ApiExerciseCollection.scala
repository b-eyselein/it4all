package model

import model.toolMains.CollectionToolMain
import play.api.libs.json.{Format, Json}

object ApiModelHelpers {

  def apiExCollFromExColl(toolMain: CollectionToolMain, ec: ExerciseCollection): ApiExerciseCollection =
    ApiExerciseCollection(ec.id, toolMain.urlPart, ec.title, ec.author, ec.text, ec.state, ec.shortName)


  val apiExCollJsonFormat: Format[ApiExerciseCollection] = Json.format[ApiExerciseCollection]

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

final case class ApiExerciseCollection(
  id: Int,
  toolId: String,
  title: String,
  author: String,
  text: String,
  state: ExerciseState,
  shortName: String
)
