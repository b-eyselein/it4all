package model.tools.collectionTools

import model._
import model.core.LongText


trait ExTag extends enumeratum.EnumEntry {

  val buttonContent: String

  val title: String

}

trait Exercise[SampleSolutionType <: SampleSolution[_]] {

  val id: Int

  val collectionId: Int

  val toolId: String

  val semanticVersion: SemanticVersion

  val title: String

  val author: String

  val text: LongText

  val state: ExerciseState


  val sampleSolutions: Seq[SampleSolutionType]


  def tags: Seq[ExTag] = Seq[ExTag]()

}

trait FileExercise extends Exercise[FilesSampleSolution]

trait StringExercise extends Exercise[StringSampleSolution]
