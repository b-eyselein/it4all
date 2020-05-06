package initialData.programming

import initialData.InitialData
import initialData.programming.coll_1.ProgrammingColl1Ex1.programmingColl1Ex1
import initialData.programming.coll_1.ProgrammingColl1Ex2.ProgrammingColl1Ex2
import model.ExerciseCollection
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming.{ProgSolution, ProgrammingExerciseContent}

object ProgrammingInitialData extends InitialData[ProgSolution, ProgrammingExerciseContent] {

  override protected val toolId = "programming"

  override val data: Seq[(ExerciseCollection, Seq[ProgrammingExercise])] = Seq(
    (
      ExerciseCollection(collectionId = 1, toolId, title = "Zahlen", authors = Seq("bje40dc"), text = "TODO"),
      Seq(programmingColl1Ex1, ProgrammingColl1Ex2)
    ),
    (
      ExerciseCollection(collectionId = 2, toolId, title = "Strings", authors = Seq("bje40dc"), text = "TODO"),
      Seq.empty
    ),
    (
      ExerciseCollection(collectionId = 3, toolId, title = "Bedingungen", authors = Seq("bje40dc"), text = "TODO"),
      Seq.empty
    ),
    (
      ExerciseCollection(collectionId = 4, toolId, title = "Listen", authors = Seq("bje40dc"), text = "TODO"),
      Seq.empty
    ),
    (
      ExerciseCollection(collectionId = 5, toolId, title = "Tupel und Dicts", authors = Seq("bje40dc"), text = "TODO"),
      Seq.empty
    ),
    (
      ExerciseCollection(collectionId = 6, toolId, title = "Funktionen", authors = Seq("bje40dc"), text = "TODO"),
      Seq.empty
    ),
    (
      ExerciseCollection(collectionId = 7, toolId, title = "Klassen", authors = Seq("bje40dc"), text = "TODO"),
      Seq.empty
    ),
    (
      ExerciseCollection(collectionId = 8, toolId, title = "Unit Testing", authors = Seq("bje40dc"), text = "TODO"),
      Seq.empty
    )
  )

}
