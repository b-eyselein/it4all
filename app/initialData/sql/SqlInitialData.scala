package initialData.sql

import initialData.InitialData
import initialData.sql.SqlColl1Exes01To10.sqlColl01To10
import initialData.sql.SqlColl2Exes01To10.sqlColl2Exes01To10
import initialData.sql.SqlColl2Exes11To20.sqlColl2Exes11To20
import initialData.sql.SqlColl2Exes21To30.sqlColl2Exes21To30
import initialData.sql.SqlColl2Exes31To40.sqlColl2Exes31To40
import initialData.sql.SqlColl2Exes41To50.sqlColl2Exes41To50
import model.ExerciseCollection
import model.tools.sql.SqlExerciseContent
import model.tools.sql.SqlTool.SqlExercise

object SqlInitialData extends InitialData[String, SqlExerciseContent] {

  override protected val toolId: String = "sql"

  private val sql_coll_2_exes: Seq[SqlExercise] = Seq(
    sqlColl2Exes01To10,
    sqlColl2Exes11To20,
    sqlColl2Exes21To30,
    sqlColl2Exes31To40,
    sqlColl2Exes41To50
  ).flatten

  override val data: Seq[(ExerciseCollection, Seq[SqlExercise])] = Seq(
    (
      ExerciseCollection(
        1,
        toolId,
        title = "Angestellte",
        authors = Seq("bje40dc"),
        text = "Dieses Szenario beschreibt die Datenbank einer kleinen Firma."
      ),
      sqlColl01To10
    ),
    (
      ExerciseCollection(2, toolId, title = "Amazon", authors = Seq("bje40dc"), text = ""),
      sql_coll_2_exes
    )
  )

}
