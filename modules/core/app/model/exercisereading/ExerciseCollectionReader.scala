package model.exercisereading;

import io.ebean.Finder;
import model.exercise.Exercise;
import model.exercise.ExerciseCollection;
import scala.collection.JavaConverters._
import com.fasterxml.jackson.databind.JsonNode
import model.StringConsts._

abstract class ExerciseCollectionReader[E <: Exercise, C <: ExerciseCollection[E]](et: String, f: Finder[Integer, C], cf: Class[_])
    extends JsonReader[C](et, f, cf) {

  override def save(collection: C) {
    collection.save();
    collection.getExercises().asScala.foreach(_.save());
  }

  def update(coll: C, node: JsonNode) {
    coll.title = node.get(TITLE_NAME).asText
    coll.author = node.get(AUTHOR_NAME).asText
    coll.text = JsonReader.readTextArray(node.get(TEXT_NAME), "");
  }

}
