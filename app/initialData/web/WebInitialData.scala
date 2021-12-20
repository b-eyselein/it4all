package initialData.web

import initialData.web.coll_1.WebColl1Ex1.webColl1Ex1
import initialData.web.coll_1.WebColl1Ex2.webColl1Ex2
import initialData.web.coll_1.WebColl1Ex3.webColl1Ex3
import initialData.web.coll_1.WebColl1Ex4.webColl1Ex4
import initialData.web.coll_1.WebColl1Ex5.webColl1Ex5
import initialData.web.coll_2.WebColl2Ex1.webColl2Ex1
import initialData.web.coll_2.WebColl2Ex2.webColl2Ex2
import initialData.web.coll_2.WebColl2Ex3.webColl2Ex3
import initialData.{InitialCollection, InitialData, InitialFilesExercise}
import model.tools.web.WebExerciseContent

abstract class WebInitialExercise(collectionId: Int, exerciseId: Int) extends InitialFilesExercise("web", collectionId, exerciseId) {}

object WebInitialData extends InitialData[WebExerciseContent] {

  private val toolId = "web"

  override val initialData: Seq[InitialCollection[WebExerciseContent]] = Seq(
    InitialCollection(
      collectionId = 1,
      title = "Html Elemente",
      authors = Seq("bje40dc"),
      exercises = Seq(webColl1Ex1, webColl1Ex2, webColl1Ex3, webColl1Ex4, webColl1Ex5)
    ),
    InitialCollection(
      collectionId = 2,
      title = "Js Basics",
      authors = Seq("bje40dc"),
      exercises = Seq(webColl2Ex1, webColl2Ex2, webColl2Ex3)
    )
  )

}
