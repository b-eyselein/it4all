package model.exercisereading;

import static model.StringConsts.AUTHOR_NAME;
import static model.StringConsts.TEXT_NAME;
import static model.StringConsts.TITLE_NAME;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;

import io.ebean.Finder;
import model.JsonReadable;
import model.StringConsts;
import model.exercise.Exercise;
import play.data.DynamicForm;

public abstract class ExerciseReader<E extends Exercise> extends JsonReader<E> {

  public Path baseTargetDir;

  public ExerciseReader(String theExerciseType, Finder<Integer, E> theFinder, Class<?> theClassFor) {
    super(theExerciseType, theFinder, theClassFor);
    baseTargetDir = Paths.get("/data", "samples", exerciseType());
  }

  public static <T extends JsonReadable> int findMinimalNotUsedId(Finder<Integer, T> finder) {
    // FIXME: this is probably a ugly hack...
    final List<T> exercises = finder.all();

    Collections.sort(exercises, (q1, q2) -> q1.getId() - q2.getId());

    if(exercises.isEmpty())
      return 1;

    for(int i = 0; i < exercises.size() - 1; i++)
      if(exercises.get(i).getId() < exercises.get(i + 1).getId() - 1)
        return exercises.get(i).getId() + 1;

    return exercises.get(exercises.size() - 1).getId() + 1;
  }

  public static <V> List<V> readArray(JsonNode arrayNode, Function<JsonNode, V> mappingFunction) {
    return StreamSupport.stream(arrayNode.spliterator(), true).map(mappingFunction).collect(Collectors.toList());
  }

  public E getOrInstantiateExercise(int id) {
    return Optional.ofNullable(finder().byId(id)).orElse(instantiateExercise(id));
  }

  public E initFromForm(int id, DynamicForm form) {
    final E exercise = getOrInstantiateExercise(id);

    exercise.setTitle(form.get(StringConsts.TITLE_NAME));
    exercise.setAuthor(form.get(StringConsts.AUTHOR_NAME));
    exercise.setText(form.get(StringConsts.TEXT_NAME));

    initRemainingExFromForm(exercise, form);

    return exercise;
  }

  public abstract void initRemainingExFromForm(E exercise, DynamicForm form);

  public void update(E exercise, JsonNode node) {
    exercise.title = node.get(TITLE_NAME).asText();
    exercise.author = node.get(AUTHOR_NAME).asText();
    exercise.text = readTextArray(node.get(TEXT_NAME), "");
  }

  protected abstract void updateExercise(E exercise, JsonNode exerciseNode);

}
