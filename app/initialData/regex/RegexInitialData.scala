package initialData.regex

import initialData.InitialData
import initialData.regex.coll_1.RegexColl1Ex1.regexColl1Ex1
import initialData.regex.coll_1.RegexColl1Ex2.regexColl1Ex2
import initialData.regex.coll_1.RegexColl1Ex3.regexColl1Ex3
import initialData.regex.coll_1.RegexColl1Ex4.regexColl1Ex4
import initialData.regex.coll_1.RegexColl1Ex5.regexColl1Ex5
import initialData.regex.coll_2.RegexColl1Ex2.RegexColl2Ex2
import initialData.regex.coll_2.RegexColl2Ex1.regexColl2Ex1
import model.ExerciseCollection
import model.tools.regex.RegexExerciseContent
import model.tools.regex.RegexTool.RegexExercise

object RegexInitialData extends InitialData[RegexExerciseContent] {

  override protected val toolId: String = "regex"

  override val data: Seq[(ExerciseCollection, Seq[RegexExercise])] = Seq(
    (
      ExerciseCollection(1, toolId, title = "Zahlen und Fakten", authors = Seq("bje40dc"), text = ""),
      Seq(regexColl1Ex1, regexColl1Ex2, regexColl1Ex3, regexColl1Ex4, regexColl1Ex5)
    ),
    (
      ExerciseCollection(2, toolId, title = "Informationsextraktion", authors = Seq("bje40dc"), text = ""),
      Seq(regexColl2Ex1, RegexColl2Ex2)
    )
  )

}
