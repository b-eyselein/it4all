package model

import scala.collection.JavaConverters.asScalaBufferConverter

import com.fasterxml.jackson.databind.JsonNode

import model.exercise.{ SqlExercise, SqlExerciseType, SqlSample, SqlSampleKey }
import model.exercisereading.{ ExerciseReader, JsonReader }
import play.data.DynamicForm
import play.libs.Json

object SqlExerciseReader extends ExerciseReader[SqlExercise]("sql", SqlExercise.finder, classOf[Array[SqlExercise]]) {

  def readSampleSolution(sampleSolNode: JsonNode): SqlSample = {
    val key = Json.fromJson(sampleSolNode.get(StringConsts.KEY_NAME), classOf[SqlSampleKey])

    var sample = SqlSample.finder.byId(key)
    if (sample == null)
      sample = new SqlSample(key)

    sample.sample = JsonReader.readAndJoinTextArray(sampleSolNode.get("sample"), "\n")

    return sample
  }

  override def initRemainingExFromForm(exercise: SqlExercise, form: DynamicForm) {
    // TODO Auto-generated method stub

    exercise.exerciseType = SqlExerciseType.valueOf(form.get(StringConsts.EXERCISE_TYPE))

    // exercise.samples = readArray(form.get(StringConsts.SAMPLES_NAME),
    // SqlExerciseReader::readSampleSolution)
    exercise.hint = form.get("hint")
    // exercise.tags = String.join(SqlExercise.SAMPLE_JOIN_CHAR,
    // parseJsonArrayNode(form.get("tags")))
  }

  override def instantiate(id: Int) = new SqlExercise(id)

  override def save(exercise: SqlExercise) {
    exercise.save()
    exercise.samples.asScala.foreach(_.save)
  }

  override def updateExercise(exercise: SqlExercise, exerciseNode: JsonNode) {
    exercise.exerciseType = SqlExerciseType.valueOf(exerciseNode.get(StringConsts.EXERCISE_TYPE).asText())

    exercise.samples = ExerciseReader.readArray(exerciseNode.get(StringConsts.SAMPLES_NAME), SqlExerciseReader.readSampleSolution(_))
    exercise.hint = exerciseNode.get("hint").asText()
    exercise.tags = JsonReader.readAndJoinTextArray(exerciseNode.get("tags"), SqlExercise.SAMPLE_JOIN_CHAR)
  }

}
