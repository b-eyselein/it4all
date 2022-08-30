package initialData.web

import initialData.web.coll_1.WebColl1Ex1.webColl1Ex1
import initialData.web.coll_1.WebColl1Ex2.webColl1Ex2
import initialData.web.coll_1.WebColl1Ex3.webColl1Ex3
import initialData.web.coll_1.WebColl1Ex4.webColl1Ex4
import initialData.web.coll_1.WebColl1Ex5.webColl1Ex5
import initialData.web.coll_2.WebColl2Ex1.webColl2Ex1
import initialData.web.coll_2.WebColl2Ex2.webColl2Ex2
import initialData.web.coll_2.WebColl2Ex3.webColl2Ex3
import initialData.{InitialCollection, InitialData, InitialFilesExerciseContainer}
import model.tools.web.WebExerciseContent

abstract class WebInitialExerciseContainer(collectionId: Int, exerciseId: Int) extends InitialFilesExerciseContainer("web", collectionId, exerciseId)

object WebInitialData extends InitialData[WebExerciseContent] {

  override val initialData: Map[Int, InitialCollection[WebExerciseContent]] = Map(
    1 -> InitialCollection(
      title = "Html Elemente",
      initialExercises = Map(
        1 -> webColl1Ex1,
        2 -> webColl1Ex2,
        3 -> webColl1Ex3,
        4 -> webColl1Ex4,
        5 -> webColl1Ex5
      )
    ),
    2 -> InitialCollection(
      title = "Js Basics",
      initialExercises = Map(
        1 -> webColl2Ex1,
        2 -> webColl2Ex2,
        3 -> webColl2Ex3
      )
    )
  )

}
