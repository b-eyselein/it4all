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

  // FIXME: use generic ExerciseContent?
  content: JsValue,
)


final case class SampleSolution[SolType](id: Int, sample: SolType)

trait ExerciseContent {

  type SolType

  val sampleSolutions: Seq[SampleSolution[SolType]]

  def tags: Seq[ExTag] = Seq[ExTag]()

}

trait FileExerciseContent extends ExerciseContent {

  override type SolType = Seq[ExerciseFile]

}

trait StringExerciseContent extends ExerciseContent {

  override type SolType = String

}
