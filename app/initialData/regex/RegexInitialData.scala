package initialData.regex

import initialData.regex.coll_1._
import initialData.regex.coll_2.{RegexColl2Ex1, RegexColl2Ex2}
import initialData.{InitialCollection, InitialData}
import model.tools.regex.RegexExerciseContent

object RegexInitialData extends InitialData[RegexExerciseContent] {

  override val initialData = Map(
    1 -> InitialCollection(
      title = "Zahlen und Fakten",
      authors = Seq("bje40dc"),
      initialExercises = Map(
        1 -> RegexColl1Ex1,
        2 -> RegexColl1Ex2,
        3 -> RegexColl1Ex3,
        4 -> RegexColl1Ex4,
        5 -> RegexColl1Ex5
      )
    ),
    2 -> InitialCollection(
      title = "Informationsextraktion",
      authors = Seq("bje40dc"),
      initialExercises = Map(
        1 -> RegexColl2Ex1,
        2 -> RegexColl2Ex2
      )
    )
  )

}
