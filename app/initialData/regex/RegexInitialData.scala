package initialData.regex

import initialData.regex.coll_1.RegexColl1Ex1.regexColl1Ex1
import initialData.regex.coll_1.RegexColl1Ex2.regexColl1Ex2
import initialData.regex.coll_1.RegexColl1Ex3.regexColl1Ex3
import initialData.regex.coll_1.RegexColl1Ex4.regexColl1Ex4
import initialData.regex.coll_1.RegexColl1Ex5.regexColl1Ex5
import initialData.regex.coll_2.RegexColl1Ex2.RegexColl2Ex2
import initialData.regex.coll_2.RegexColl2Ex1.regexColl2Ex1
import initialData.{InitialCollection, InitialData}
import model.tools.regex.RegexExerciseContent

object RegexInitialData extends InitialData[RegexExerciseContent] {

  override val initialData = Seq(
    InitialCollection(
      collectionId = 1,
      title = "Zahlen und Fakten",
      authors = Seq("bje40dc"),
      exercises = Seq(regexColl1Ex1, regexColl1Ex2, regexColl1Ex3, regexColl1Ex4, regexColl1Ex5)
    ),
    InitialCollection(
      collectionId = 2,
      title = "Informationsextraktion",
      authors = Seq("bje40dc"),
      exercises = Seq(regexColl2Ex1, RegexColl2Ex2)
    )
  )

}
