package initialData.web

import initialData.InitialData
import initialData.web.coll_1.WebColl1Ex1.webColl1Ex1
import initialData.web.coll_1.WebColl1Ex2.webColl1Ex2
import initialData.web.coll_1.WebColl1Ex3.webColl1Ex3
import initialData.web.coll_1.WebColl1Ex4.webColl1Ex4
import initialData.web.coll_1.WebColl1Ex5.webColl1Ex5
import initialData.web.coll_2.WebColl2Ex1.webColl2Ex1
import initialData.web.coll_2.WebColl2Ex2.webColl2Ex2
import initialData.web.coll_2.WebColl2Ex3.webColl2Ex3
import model.ExerciseCollection
import model.tools.web.WebTool.WebExercise
import model.tools.web.{WebExerciseContent, WebSolution}

object WebInitialData extends InitialData[WebSolution, WebExerciseContent] {

  override protected val toolId = "web"

  override val data: Seq[(ExerciseCollection, Seq[WebExercise])] = Seq(
    (
      ExerciseCollection(collectionId = 1, toolId, title = "Html Elemente", authors = Seq("bje40dc"), text = "TODO"),
      Seq(webColl1Ex1, webColl1Ex2, webColl1Ex3, webColl1Ex4, webColl1Ex5)
    ),
    (
      ExerciseCollection(collectionId = 2, toolId, title = "Js Basics", authors = Seq("bje40dc"), text = "TODO"),
      Seq(webColl2Ex1, webColl2Ex2, webColl2Ex3)
    )
  )

}
