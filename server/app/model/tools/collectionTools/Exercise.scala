package model.tools.collectionTools

import model._
import model.core.LongText
import play.api.libs.json.JsValue


trait ExTag extends enumeratum.EnumEntry {

  val buttonContent: String

  val title: String

}

final case class Exercise(
  id: Int,
  collectionId: Int,
  toolId: String,
  semanticVersion: SemanticVersion,
  title: String,
  author: String,
  text: LongText,
  state: ExerciseState,

  // FIXME: make ContentType => JsValue!
  content: JsValue,
)

trait ExerciseContent {

  type SampleSolType <: SampleSolution[_]

  val sampleSolutions: Seq[SampleSolType]

  def tags: Seq[ExTag] = Seq[ExTag]()

}

trait FileExerciseContent extends ExerciseContent {

  override type SampleSolType = FilesSampleSolution

}

trait StringExerciseContent extends ExerciseContent {

  override type SampleSolType = StringSampleSolution

}
