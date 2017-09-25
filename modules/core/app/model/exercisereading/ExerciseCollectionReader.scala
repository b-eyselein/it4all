package model.exercisereading

import scala.collection.JavaConverters.asScalaBufferConverter

import com.fasterxml.jackson.databind.JsonNode

import io.ebean.Finder
import model.StringConsts.{ AUTHOR_NAME, TEXT_NAME, TITLE_NAME }
import model.exercise.{ Exercise, ExerciseCollection }

abstract class ExerciseCollectionReader[E <: Exercise, C <: ExerciseCollection[E]](et: String, f: Finder[Integer, C], cf: Class[_])
  extends JsonReader[C](et, f, cf) {

  override def save(collection: C) {
    collection.save()
    collection.getExercises().asScala.foreach(_.save())
  }

  def update(coll: C, node: JsonNode) {
    coll.title = node.get(TITLE_NAME).asText
    coll.author = node.get(AUTHOR_NAME).asText
    coll.text = JsonReader.readTextArray(node.get(TEXT_NAME)).mkString("")
  }

}
