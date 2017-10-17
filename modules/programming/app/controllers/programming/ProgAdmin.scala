package controllers.programming

import javax.inject.Inject

import controllers.core.AExerciseAdminController
import model.{ProgExercise, ProgExerciseReader}
import play.data.FormFactory

class ProgAdmin @Inject()(f: FormFactory)
  extends AExerciseAdminController[ProgExercise](f, ProgToolObject, ProgExercise.finder, ProgExerciseReader.getInstance())
