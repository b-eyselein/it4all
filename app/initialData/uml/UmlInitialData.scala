package initialData.uml

import initialData.uml.coll_1.UmlColl1Ex1.umlColl1Ex1
import initialData.uml.coll_1.umlColl1Ex2.umlColl1Ex2
import initialData.{InitialCollection, InitialData}
import model.tools.uml.UmlExerciseContent

object UmlInitialData {

  val initialData: InitialData[UmlExerciseContent] = InitialData[UmlExerciseContent](
    initialCollections = Map(
      1 -> InitialCollection(
        title = "Uml Basics",
        initialExercises = Map(
          1 -> umlColl1Ex1,
          2 -> umlColl1Ex2
        )
      )
    )
  )

}
