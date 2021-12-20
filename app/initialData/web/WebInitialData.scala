package initialData.web

import initialData.web.coll_1.WebColl1Ex1.webColl1Ex1
import initialData.web.coll_1.WebColl1Ex2.webColl1Ex2
import initialData.web.coll_1.WebColl1Ex3.webColl1Ex3
import initialData.web.coll_1.WebColl1Ex4.webColl1Ex4
import initialData.web.coll_1.WebColl1Ex5.webColl1Ex5
import initialData.web.coll_2.WebColl2Ex1.webColl2Ex1
import initialData.web.coll_2.WebColl2Ex2.webColl2Ex2
import initialData.web.coll_2.WebColl2Ex3.webColl2Ex3
import initialData.{InitialData, InitialFilesExercise}
import model.ExerciseCollection
import model.tools.web.WebExerciseContent
import model.tools.web.WebTool.WebExercise

abstract class WebInitialExercise(collectionId: Int, exerciseId: Int) extends InitialFilesExercise("web", collectionId, exerciseId) {}

object WebInitialData extends InitialData[WebExerciseContent] {

  override protected val toolId = "web"

  private val webColl01 = ExerciseCollection(collectionId = 1, toolId, title = "Html Elemente", authors = Seq("bje40dc"))

  private val webColl01Exes = Seq(webColl1Ex1, webColl1Ex2, webColl1Ex3, webColl1Ex4, webColl1Ex5)

  private val webColl02 = ExerciseCollection(collectionId = 2, toolId, title = "Js Basics", authors = Seq("bje40dc"))

  private val webColl02Exes = Seq(webColl2Ex1, webColl2Ex2, webColl2Ex3)

  override val exerciseData: Seq[(ExerciseCollection, Seq[WebExercise])] = Seq(
    (webColl01, webColl01Exes),
    (webColl02, webColl02Exes)
  )

}
