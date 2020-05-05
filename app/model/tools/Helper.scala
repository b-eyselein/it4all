package model.tools

import model.{Exercise, ExerciseContent}

object Helper {

  type UntypedExercise = Exercise[_, _ <: ExerciseContent[_]]

}
