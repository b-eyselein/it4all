package model.tools

import model.{Exercise, ExerciseContent, ExerciseContentWithParts}

object Helper {

  type UntypedExercise = Exercise[_ <: ExerciseContent]

  type UntypedExerciseWithParts = Exercise[_ <: ExerciseContentWithParts]

}
