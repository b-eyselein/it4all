package model

import java.nio.file.Path
import javax.persistence.Entity

import com.fasterxml.jackson.annotation.{JsonIgnore, JsonProperty}
import com.fasterxml.jackson.databind.JsonNode
import controllers.spread.SpreadToolObject
import io.ebean.Finder
import model.exercise.Exercise
import model.exercisereading.ExerciseReader
import play.data.DynamicForm
import play.twirl.api.Html

import scala.util.Try

@Entity
class SpreadExercise(i: Int) extends Exercise(i) {

  @JsonProperty(required = true)
  var sampleFilename: String = _

  @JsonProperty(required = true)
  var templateFilename: String = _

  @JsonIgnore
  override def renderRest(fileResults: List[Try[Path]]): Html = new Html(
    s"""<td>$sampleFilename</td>
       |<td>$templateFilename</td>""".stripMargin)


  //  @JsonIgnore
  //  override def renderEditRest(isCreation: Boolean): Html = views.html.editXmlExRest(this, isCreation)

}

object SpreadExercise {
  val finder: Finder[Integer, SpreadExercise] = new Finder(classOf[SpreadExercise])
}

object SpreadExerciseReader
  extends ExerciseReader[SpreadExercise]("spread", SpreadExercise.finder, classOf[Array[SpreadExercise]]) {

  override def initRemainingExFromForm(exercise: SpreadExercise, form: DynamicForm) {
    exercise.templateFilename = form.get(StringConsts.TEMPALTE_FILENAME)
    exercise.sampleFilename = form.get(StringConsts.SAMPLE_FILENAME)
  }

  override def instantiate(id: Int) = new SpreadExercise(id)

  override def save(exercise: SpreadExercise) {
    exercise.save()
    // FIXME: implement with toolObject
    //    List("xlsx", "ods").map(exercise.sampleFilename + "." + _).foreach(fileName => {
    //      val tr = checkOrCreateSampleFile(exercise, toolObject, fileName) match {
    //        case Failure(e) => Logger.error(s"An error has occured while saving the sample file $fileName", e)
    //        case Success(s) => Logger.info(s"Solution file $fileName was created successfully")
    //      }
    //    })
  }


  override def checkFiles(exercise: SpreadExercise): List[scala.util.Try[java.nio.file.Path]] =
    for {file <- List(exercise.sampleFilename, exercise.templateFilename)
         ending <- List(".xsls", "ods")
         res = checkOrCreateSampleFile(exercise, SpreadToolObject, file + "." + ending)
    } yield res

  override def updateExercise(exercise: SpreadExercise, exerciseNode: JsonNode) {
    exercise.sampleFilename = exerciseNode.get(StringConsts.SAMPLE_FILENAME).asText
    exercise.templateFilename = exerciseNode.get(StringConsts.TEMPALTE_FILENAME).asText
  }

}
