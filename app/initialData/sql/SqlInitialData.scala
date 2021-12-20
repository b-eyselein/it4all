package initialData.sql

import initialData.sql.SqlColl1Exes01To10.sqlColl01Exes01To10
import initialData.sql.SqlColl2Exes01To10.sqlColl2Exes01To10
import initialData.sql.SqlColl2Exes11To20.sqlColl2Exes11To20
import initialData.sql.SqlColl2Exes21To30.sqlColl2Exes21To30
import initialData.sql.SqlColl2Exes31To40.sqlColl2Exes31To40
import initialData.sql.SqlColl2Exes41To50.sqlColl2Exes41To50
import initialData.{InitialCollection, InitialData}
import model.tools.sql.SqlExerciseContent

object SqlInitialData extends InitialData[SqlExerciseContent] {

  override val initialData = Seq(
    InitialCollection(
      collectionId = 1,
      title = "Angestellte",
      authors = Seq("bje40dc"),
      sqlColl01Exes01To10
    ),
    InitialCollection(
      collectionId = 2,
      title = "Amazon",
      authors = Seq("bje40dc"),
      exercises = Seq(sqlColl2Exes01To10, sqlColl2Exes11To20, sqlColl2Exes21To30, sqlColl2Exes31To40, sqlColl2Exes41To50).flatten
    )
  )

}
