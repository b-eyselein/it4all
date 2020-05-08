package initialData.programming

import initialData.InitialData
import initialData.programming.coll_1.ProgrammingColl1Ex1.programmingColl1Ex1
import initialData.programming.coll_1.ProgrammingColl1Ex2.programmingColl1Ex2
import initialData.programming.coll_1.ProgrammingColl1Ex3.programmingColl1Ex3
import initialData.programming.coll_1.ProgrammingColl1Ex4.programmingColl1Ex4
import initialData.programming.coll_2.ProgrammingColl2Ex1.programmingColl2Ex1
import initialData.programming.coll_2.ProgrammingColl2Ex2.programmingColl2Ex2
import initialData.programming.coll_2.ProgrammingColl2Ex3.programmingColl2Ex3
import initialData.programming.coll_2.ProgrammingColl2Ex4.programmingColl2Ex4
import initialData.programming.coll_2.ProgrammingColl2Ex5.programmingColl2Ex5
import initialData.programming.coll_2.ProgrammingColl2Ex6.programmingColl2Ex6
import initialData.programming.coll_2.ProgrammingColl2Ex7.programmingColl2Ex7
import initialData.programming.coll_2.ProgrammingColl2Ex8.programmingColl2Ex8
import model.ExerciseCollection
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming.{ProgSolution, ProgrammingExerciseContent}

object ProgrammingInitialData extends InitialData[ProgSolution, ProgrammingExerciseContent] {

  override protected val toolId = "programming"

  override val data: Seq[(ExerciseCollection, Seq[ProgrammingExercise])] = Seq(
    (
      ExerciseCollection(collectionId = 1, toolId, title = "Zahlen", authors = Seq("bje40dc"), text = "TODO"),
      Seq(programmingColl1Ex1, programmingColl1Ex2, programmingColl1Ex3, programmingColl1Ex4)
    ),
    (
      ExerciseCollection(collectionId = 2, toolId, title = "Strings", authors = Seq("bje40dc"), text = "TODO"),
      Seq(
        programmingColl2Ex1,
        programmingColl2Ex2,
        programmingColl2Ex3,
        programmingColl2Ex4,
        programmingColl2Ex5,
        programmingColl2Ex6,
        programmingColl2Ex7,
        programmingColl2Ex8
      )
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
