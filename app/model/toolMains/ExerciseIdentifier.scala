package model.toolMains

import model.SemanticVersion

trait ExerciseIdentifier {

  val exId: Int

}

final case class SingleExerciseIdentifier(exId: Int, exSemVer: SemanticVersion) extends ExerciseIdentifier

final case class CollectionExIdentifier(collId: Int, exId: Int) extends ExerciseIdentifier
