package initialData.sql

import initialData.InitialData
import initialData.sql.SqlColl1Exes01To10.sqlColl01To10
import initialData.sql.SqlColl2Exes01To10.sqlColl2Exes01To10
import initialData.sql.SqlColl2Exes11To20.sqlColl2Exes11To20
import initialData.sql.SqlColl2Exes21To30.sqlColl2Exes21To30
import initialData.sql.SqlColl2Exes31To40.sqlColl2Exes31To40
import initialData.sql.SqlColl2Exes41To50.sqlColl2Exes41To50
import model.ExerciseCollection
import model.lesson.{Lesson, LessonContent}
import model.tools.sql.SqlExerciseContent
import model.tools.sql.SqlTool.SqlExercise

object SqlInitialData extends InitialData[SqlExerciseContent] {

  override protected val toolId: String = "sql"

  private val sqlCollection01 = ExerciseCollection(1, toolId, title = "Angestellte", authors = Seq("bje40dc"))

  private val sqlCollection02 = ExerciseCollection(2, toolId, title = "Amazon", authors = Seq("bje40dc"))

  private val sql_coll_2_exes: Seq[SqlExercise] =
    Seq(sqlColl2Exes01To10, sqlColl2Exes11To20, sqlColl2Exes21To30, sqlColl2Exes31To40, sqlColl2Exes41To50).flatten

  override val exerciseData: Seq[(ExerciseCollection, Seq[SqlExercise])] = Seq(
    (sqlCollection01, sqlColl01To10),
    (sqlCollection02, sql_coll_2_exes)
  )

  private val sqlLesson01 = Lesson(1, toolId, "Grundlagen SQL", "In dieser Lektion geht es um die Grundlagen von SQL.")

  private val sqlLesson01Content = Seq()

  override val lessonData: Seq[(Lesson, Seq[LessonContent])] = Seq(
    (sqlLesson01, sqlLesson01Content)
  )

}
