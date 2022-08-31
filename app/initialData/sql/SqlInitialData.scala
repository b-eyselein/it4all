package initialData.sql

import initialData.sql.SqlColl1Exes01To10._
import initialData.sql.SqlColl2Exes01To10._
import initialData.sql.SqlColl2Exes11To20._
import initialData.sql.SqlColl2Exes21To30._
import initialData.sql.SqlColl2Exes31To40._
import initialData.sql.SqlColl2Exes41To50._
import initialData.{InitialCollection, InitialData}
import model.tools.sql.{SqlExerciseContent, SqlTopics}

object SqlInitialData {

  val initialData: InitialData[SqlExerciseContent] = InitialData[SqlExerciseContent](
    topics = SqlTopics.values,
    initialCollections = Map(
      1 -> InitialCollection(
        title = "Angestellte",
        initialExercises = Map(
          1 -> sql_coll_1_ex_1,
          2 -> sql_coll_1_ex_2,
          3 -> sql_coll_1_ex_3,
          4 -> sql_coll_1_ex_4,
          5 -> sql_coll_1_ex_5,
          6 -> sql_coll_1_ex_6,
          7 -> sql_coll_1_ex_7,
          8 -> sql_coll_1_ex_8,
          9 -> sql_coll_1_ex_9
        )
      ),
      2 -> InitialCollection(
        title = "Amazon",
        initialExercises = Map(
          1  -> sql_coll_2_ex_01,
          2  -> sql_coll_2_ex_02,
          3  -> sql_coll_2_ex_03,
          4  -> sql_coll_2_ex_04,
          5  -> sql_coll_2_ex_05,
          6  -> sql_coll_2_ex_06,
          7  -> sql_coll_2_ex_07,
          8  -> sql_coll_2_ex_08,
          9  -> sql_coll_2_ex_09,
          10 -> sql_coll_2_ex_10,
          11 -> sql_coll_2_ex_11,
          12 -> sql_coll_2_ex_12,
          13 -> sql_coll_2_ex_13,
          14 -> sql_coll_2_ex_14,
          15 -> sql_coll_2_ex_15,
          16 -> sql_coll_2_ex_16,
          17 -> sql_coll_2_ex_17,
          18 -> sql_coll_2_ex_18,
          19 -> sql_coll_2_ex_19,
          20 -> sql_coll_2_ex_20,
          21 -> sql_coll_2_ex_21,
          22 -> sql_coll_2_ex_22,
          23 -> sql_coll_2_ex_23,
          24 -> sql_coll_2_ex_24,
          25 -> sql_coll_2_ex_25,
          26 -> sql_coll_2_ex_26,
          27 -> sql_coll_2_ex_27,
          28 -> sql_coll_2_ex_28,
          29 -> sql_coll_2_ex_29,
          30 -> sql_coll_2_ex_30,
          31 -> sql_coll_2_ex_31,
          32 -> sql_coll_2_ex_32,
          33 -> sql_coll_2_ex_33,
          34 -> sql_coll_2_ex_34,
          35 -> sql_coll_2_ex_35,
          36 -> sql_coll_2_ex_36,
          37 -> sql_coll_2_ex_37,
          38 -> sql_coll_2_ex_38,
          39 -> sql_coll_2_ex_39,
          40 -> sql_coll_2_ex_40,
          41 -> sql_coll_2_ex_41,
          42 -> sql_coll_2_ex_42,
          43 -> sql_coll_2_ex_43,
          44 -> sql_coll_2_ex_44,
          45 -> sql_coll_2_ex_45,
          46 -> sql_coll_2_ex_46,
          47 -> sql_coll_2_ex_47,
          48 -> sql_coll_2_ex_48
        )
      )
    )
  )

}
