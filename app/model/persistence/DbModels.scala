package model.persistence

trait DbModels {

  type DbExercise

  type Exercise

  def dbExerciseFromExercise(ex: Exercise): DbExercise

}
