package model.exercisereading;

import io.ebean.Finder;
import model.exercise.Exercise;
import model.exercise.ExerciseCollection;
import scala.collection.JavaConverters._

abstract class ExerciseCollectionReader[E <: Exercise, C <: ExerciseCollection[E]](et: String, f: Finder[Integer, C], cf: Class[_])
    extends JsonReader[C](et, f, cf) {

  override def save(collection: C) {
    collection.save();
    collection.getExercises().asScala.foreach(_.save());
  }

}
