package model

import com.fasterxml.jackson.databind.JsonNode
import model.exercisereading.ExerciseReader
import model.testdata.{SampleTestData, SampleTestDataKey}
import play.libs.Json

object ProgExerciseReader extends ExerciseReader[ProgExercise]("prog", ProgExercise.finder, classOf[Array[ProgExercise]]) {

  def readSample(sampleNode: JsonNode): ProgSample = {
    val key = Json.fromJson(sampleNode.get(StringConsts.KEY_NAME), classOf[ProgSampleKey])
    val sampleString = sampleNode.get(StringConsts.SAMPLE_NAME).asText()

    Option(ProgSample.finder.byId(key)) match {
      case None => new ProgSample(key, sampleString)
      case Some(sample) => sample.updateValues(key, sampleString)
    }
  }

  def readTest(testNode: JsonNode): SampleTestData = {
    val key = Json.fromJson(testNode.get(StringConsts.KEY_NAME), classOf[SampleTestDataKey])

    val test = Option(SampleTestData.finder.byId(key)).getOrElse(new SampleTestData(key))

    test.inputs = testNode.get(StringConsts.INPUTS_NAME).asText()
    test.output = testNode.get("output").asText()
    test
  }

//  override def initRemainingExFromForm(exercise: ProgExercise, form: DynamicForm) {
  //    exercise.functionName = form.get("functionName")
  //    exercise.inputCount = Integer.parseInt(form.get("inputCount"))
  //
  //    exercise.samples = java.util.Collections.emptyList()
  //    exercise.sampleTestData = java.util.Collections.emptyList()
  //  }

  override def instantiate(id: Int) = new ProgExercise(id)

  override def save(exercise: ProgExercise) {
    exercise.save()
    exercise.sampleTestData.forEach(_.save())
  }

  override def updateExercise(exercise: ProgExercise, exerciseNode: JsonNode) {
    exercise.functionName = exerciseNode.get("functionName").asText()
    exercise.inputCount = exerciseNode.get("inputCount").asInt()

    // exercise.setSamples(
    // ExerciseReader.readArray(exerciseNode.get(StringConsts$.MODULE$.SAMPLES_NAME()),
    // ProgExerciseReader::readSample))
    // exercise
    // .setSampleTestData(ExerciseReader.readArray(exerciseNode.get("sampleTestData"),
    // ProgExerciseReader::readTest))
  }

}
