package model.tools.web.persistence

import de.uniwue.webtester.{HtmlTask, JsTask}
import model.persistence.DbExerciseFile
import model.tools.web._
import model.{ExerciseFile, SemanticVersion}

import scala.concurrent.{ExecutionContext, Future}

trait WebTableQueries {
  self: WebTableDefs =>

  protected implicit val executionContext: ExecutionContext

  import profile.api._

  // Other queries

  override def completeExForEx(collId: Int, ex: DbWebExercise): Future[WebExercise] = for {
    htmlTasks <- htmlTasksForExercise(collId, ex.id)
    jsTasks <- jsTasksForExercise(collId, ex.id)
    files <- webFilesForExercise(collId, ex.id)
    sampleSolutions <- futureSampleSolutionsForExercise(collId, ex.id)
  } yield WebDbModels.exerciseFromDbExercise(ex, htmlTasks sortBy (_.id), jsTasks sortBy (_.id), files, sampleSolutions)

  private def htmlTasksForExercise(collId: Int, exId: Int): Future[Seq[HtmlTask]] = db.run(
    htmlTasksTable
      .filter { ht => ht.exerciseId === exId && ht.collectionId === collId }
      .result
  ).map(_.map(WebDbModels.htmlTaskFromDbHtmlTask))

  private def webFilesForExercise(collId: Int, exId: Int): Future[Seq[ExerciseFile]] = db.run(
    webFilesTableQuery
      .filter { wf => wf.exerciseId === exId && wf.collectionId === collId }
      .result).map(_.map(WebDbModels.exerciseFileFromDbExerciseFile))

  private def jsTasksForExercise(collId: Int, exId: Int): Future[Seq[JsTask]] = db.run(
    jsTasksTable
      .filter { jt => jt.exerciseId === exId && jt.collectionId === collId }
      .result
  ).map(_.map(WebDbModels.jsTaskFromDbJsTask))

  // Saving

  override def saveExerciseRest(collId: Int, ex: WebExercise): Future[Boolean] = for {
    htmlTasksSaved <- saveSeq[HtmlTask](ex.siteSpec.htmlTasks, t => saveHtmlTask(ex.id, ex.semanticVersion, collId, t))
    jsTasksSaved <- saveSeq[JsTask](ex.siteSpec.jsTasks, t => saveJsTask(ex.id, ex.semanticVersion, collId, t))
    webFilesSaved <- saveWebFiles(ex.id, ex.semanticVersion, collId, ex.files)
    sampleSolutionsSaved <- futureSaveSampleSolutions(ex.id, ex.semanticVersion, collId, ex.sampleSolutions)
  } yield htmlTasksSaved && jsTasksSaved && webFilesSaved && sampleSolutionsSaved

  private def saveHtmlTask(exId: Int, exSemVer: SemanticVersion, collId: Int, htmlTask: HtmlTask): Future[Boolean] =
    db.run(htmlTasksTable += WebDbModels.dbHtmlTaskFromHtmlTask(exId, collId, htmlTask)).transform(_ == 1, identity)

  private def saveJsTask(exId: Int, exSemVer: SemanticVersion, collId: Int, jsTask: JsTask): Future[Boolean] =
    db.run(jsTasksTable += WebDbModels.dbJsTaskFromJsTask(exId, collId, jsTask)).transform(_ == 1, identity)

  private def saveWebFiles(exId: Int, exSemVer: SemanticVersion, collId: Int, webFiles: Seq[ExerciseFile]): Future[Boolean] = {
    val dbWebFiles = webFiles.map(WebDbModels.dbExerciseFileFromExerciseFile(exId, exSemVer, collId, _))

    saveSeq[DbExerciseFile](dbWebFiles, dbwf => db.run(webFilesTableQuery += dbwf))
  }

}
