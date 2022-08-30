package initialData.programming

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
import initialData.programming.coll_3.ProgrammingColl3Ex1.programmingColl3Ex1
import initialData.programming.coll_3.ProgrammingColl3Ex2.programmingColl3Ex2
import initialData.programming.coll_3.ProgrammingColl3Ex3.programmingColl3Ex3
import initialData.programming.coll_4.ProgrammingColl4Ex1.programmingColl4Ex1
import initialData.programming.coll_4.ProgrammingColl4Ex2.programmingColl4Ex2
import initialData.programming.coll_4.ProgrammingColl4Ex3.programmingColl4Ex3
import initialData.programming.coll_4.ProgrammingColl4Ex4.programmingColl4Ex4
import initialData.programming.coll_5.ProgrammingColl5Ex1.programmingColl5Ex1
import initialData.programming.coll_5.ProgrammingColl5Ex2.programmingColl5Ex2
import initialData.programming.coll_5.ProgrammingColl5Ex3.programmingColl5Ex3
import initialData.programming.coll_5.ProgrammingColl5Ex4.programmingColl5Ex4
import initialData.programming.coll_6.ProgrammingColl6Ex1.programmingColl6Ex1
import initialData.programming.coll_6.ProgrammingColl6Ex2.programmingColl6Ex2
import initialData.programming.coll_7.ProgrammingColl7Ex1.programmingColl7Ex1
import initialData.programming.coll_7.ProgrammingColl7Ex2.programmingColl7Ex2
import initialData.programming.coll_7.ProgrammingColl7Ex3.programmingColl7Ex3
import initialData.programming.coll_8.ProgrammingColl8Ex1.programmingColl8Ex1
import initialData.{InitialCollection, InitialData}
import model.tools.programming.ProgrammingExerciseContent

object ProgrammingInitialData extends InitialData[ProgrammingExerciseContent] {

  override val initialData: Map[Int, InitialCollection[ProgrammingExerciseContent]] = Map(
    1 -> InitialCollection(
      title = "Zahlen",
      initialExercises = Map(
        1 -> programmingColl1Ex1,
        2 -> programmingColl1Ex2,
        3 -> programmingColl1Ex3,
        4 -> programmingColl1Ex4
      )
    ),
    2 -> InitialCollection(
      title = "Strings",
      initialExercises = Map(
        1 -> programmingColl2Ex1,
        2 -> programmingColl2Ex2,
        3 -> programmingColl2Ex3,
        4 -> programmingColl2Ex4,
        5 -> programmingColl2Ex5,
        6 -> programmingColl2Ex6,
        7 -> programmingColl2Ex7,
        8 -> programmingColl2Ex8
      )
    ),
    3 -> InitialCollection(
      title = "Bedingungen",
      initialExercises = Map(
        1 -> programmingColl3Ex1,
        2 -> programmingColl3Ex2,
        3 -> programmingColl3Ex3
      )
    ),
    4 -> InitialCollection(
      title = "Listen",
      initialExercises = Map(
        1 -> programmingColl4Ex1,
        2 -> programmingColl4Ex2,
        3 -> programmingColl4Ex3,
        4 -> programmingColl4Ex4
      )
    ),
    5 -> InitialCollection(
      title = "Tupel und Dicts",
      initialExercises = Map(
        1 -> programmingColl5Ex1,
        2 -> programmingColl5Ex2,
        3 -> programmingColl5Ex3,
        4 -> programmingColl5Ex4
      )
    ),
    6 -> InitialCollection(
      title = "Funktionen",
      initialExercises = Map(
        1 -> programmingColl6Ex1,
        2 -> programmingColl6Ex2
      )
    ),
    7 -> InitialCollection(
      title = "Klassen",
      initialExercises = Map(
        1 -> programmingColl7Ex1,
        2 -> programmingColl7Ex2,
        3 -> programmingColl7Ex3
      )
    ),
    8 -> InitialCollection(
      title = "Unit Testing",
      initialExercises = Map(
        1 -> programmingColl8Ex1
      )
    )
  )

}
