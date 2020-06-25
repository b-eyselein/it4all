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
import model.ExerciseCollection
import model.tools.programming.ProgrammingExerciseContent
import model.tools.programming.ProgrammingTool.ProgrammingExercise

object ProgrammingInitialData extends InitialData[ProgrammingExerciseContent] {

  override protected val toolId = "programming"

  private val progColl01 = ExerciseCollection(collectionId = 1, toolId, title = "Zahlen", authors = Seq("bje40dc"))

  private val progColl01Exes = Seq(programmingColl1Ex1, programmingColl1Ex2, programmingColl1Ex3, programmingColl1Ex4)

  private val progColl02 = ExerciseCollection(collectionId = 2, toolId, title = "Strings", authors = Seq("bje40dc"))

  private val progColl02Exes = Seq(
    programmingColl2Ex1,
    programmingColl2Ex2,
    programmingColl2Ex3,
    programmingColl2Ex4,
    programmingColl2Ex5,
    programmingColl2Ex6,
    programmingColl2Ex7,
    programmingColl2Ex8
  )

  private val progColl03 = ExerciseCollection(collectionId = 3, toolId, title = "Bedingungen", authors = Seq("bje40dc"))

  private val progColl03Exes = Seq(programmingColl3Ex1, programmingColl3Ex2, programmingColl3Ex3)

  private val progColl04 = ExerciseCollection(collectionId = 4, toolId, title = "Listen", authors = Seq("bje40dc"))

  private val progColl04Exes = Seq(programmingColl4Ex1, programmingColl4Ex2, programmingColl4Ex3, programmingColl4Ex4)

  private val progColl05 =
    ExerciseCollection(collectionId = 5, toolId, title = "Tupel und Dicts", authors = Seq("bje40dc"))

  private val progColl05Exes = Seq(programmingColl5Ex1, programmingColl5Ex2, programmingColl5Ex3, programmingColl5Ex4)

  private val progColl06 = ExerciseCollection(collectionId = 6, toolId, title = "Funktionen", authors = Seq("bje40dc"))

  private val progColl06Exes = Seq(programmingColl6Ex1, programmingColl6Ex2)

  private val progColl07 = ExerciseCollection(collectionId = 7, toolId, title = "Klassen", authors = Seq("bje40dc"))

  private val progColl07Exes = Seq.empty

  private val progColl08 =
    ExerciseCollection(collectionId = 8, toolId, title = "Unit Testing", authors = Seq("bje40dc"))

  private val progColl08Exes = Seq.empty

  override val data: Seq[(ExerciseCollection, Seq[ProgrammingExercise])] = Seq(
    (progColl01, progColl01Exes),
    (progColl02, progColl02Exes),
    (progColl03, progColl03Exes),
    (progColl04, progColl04Exes),
    (progColl05, progColl05Exes),
    (progColl06, progColl06Exes),
    (progColl07, progColl07Exes),
    (progColl08, progColl08Exes)
  )

}
