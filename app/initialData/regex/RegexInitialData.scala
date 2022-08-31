package initialData.regex

import initialData.{InitialCollection, InitialData}
import model.tools.regex.RegexExerciseContent

object RegexInitialData {

  val initialData: InitialData[RegexExerciseContent] = InitialData[RegexExerciseContent](
    initialCollections = Map(
      1 -> InitialCollection(
        title = "Zahlen und Fakten",
        initialExercises = RegexColl1.initialCollections
      ),
      2 -> InitialCollection(
        title = "Informationsextraktion",
        initialExercises = RegexColl2.initialCollections
      )
    )
  )

}
