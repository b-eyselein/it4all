package initialData.uml

import initialData.uml.coll_1.UmlColl1Ex1.umlColl1Ex1
import initialData.uml.coll_1.umlColl1Ex2.umlColl1Ex2
import initialData.{InitialCollection, InitialData}
import model.tools.uml.UmlExerciseContent

object UmlInitialData extends InitialData[UmlExerciseContent] {

  override val initialData = Seq(
    InitialCollection(
      collectionId = 1,
      title = "Uml Basics",
      authors = Seq("bje40dc"),
      exercises = Seq(umlColl1Ex1, umlColl1Ex2)
    )
  )

}
