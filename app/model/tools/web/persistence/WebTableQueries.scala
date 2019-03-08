package model.tools.web.persistence

import de.uniwue.webtester.{HtmlTask, JsTask}
import model.SemanticVersion
import model.tools.web.{WebExPart, WebExParts, WebExercise}

import scala.concurrent.{ExecutionContext, Future}

trait WebTableQueries {
  self: WebTableDefs =>

  protected implicit val executionContext: ExecutionContext

  import profile.api._

  // Other queries

  override def completeExForEx(collId: Int, ex: DbWebExercise): Future[WebExercise] = for {
    htmlTasks <- htmlTasksForExercise(collId, ex.id)
    jsTasks <- jsTasksForExercise(collId, ex.id)
    sampleSolutions <- db.run(sampleSolutionsTableQuery.filter {
      s => s.exerciseId === ex.id && s.exSemVer === ex.semanticVersion
    }.result) map (_ map solutionDbModels.sampleSolFromDbSampleSol)
  } yield dbModels.exerciseFromDbExercise(ex, htmlTasks sortBy (_.id), jsTasks sortBy (_.id), sampleSolutions)

  private def htmlTasksForExercise(collId: Int, exId: Int): Future[Seq[HtmlTask]] = {

    val htmlTasksForExQuery = htmlTasksTable.filter {
      ht => ht.exerciseId === exId && ht.collectionId === collId
    }.result

    def dbHtmlAttributesForTask(taskId: Int) = attributesTable.filter {
      ha => ha.taskId === taskId && ha.exerciseId === exId && ha.collectionId === collId
    }.result

    db.run(htmlTasksForExQuery) flatMap { dbHtmlTasks: Seq[DbHtmlTask] =>
      Future.sequence(dbHtmlTasks map { dbHtmlTask =>
        for {
          attributes <- db.run(dbHtmlAttributesForTask(dbHtmlTask.id))
        } yield dbModels.htmlTaskFromDbHtmlTask(dbHtmlTask, attributes)
      })
    }
  }

  private def jsTasksForExercise(collId: Int, exId: Int): Future[Seq[JsTask]] = {
    val jsTasksForExQuery = jsTasksTable.filter {
      jt => jt.exerciseId === exId && jt.collectionId === collId
    }.result

    db.run(jsTasksForExQuery) flatMap { dbJsTasks: Seq[DbJsTask] =>
      Future.sequence(dbJsTasks.map { dbJsTask =>
        for {
          conditions <- dbJsConditionsForTask(collId, exId, dbJsTask.id)
        } yield dbModels.jsTaskFromDbJsTask(dbJsTask, conditions)
      })
    }
  }

  private def dbJsConditionsForTask(collId: Int, exId: Int, taskId: Int): Future[Seq[(DbJsCondition, Seq[DbJsConditionAttribute])]] = {
    def futureJsConditionsForTask: Future[Seq[DbJsCondition]] = db.run(jsConditionsTableQuery
      .filter {
        jc => jc.collectionId === collId && jc.exerciseId === exId && jc.taskId === taskId
      }.result)

    futureJsConditionsForTask.flatMap { jsConditions: Seq[DbJsCondition] =>
      Future.sequence(jsConditions.map { jsCond =>
        for {
          attributes <- dbJsConditionAttributesForCondition(collId, exId, taskId, jsCond.id)
        } yield (jsCond, attributes)
      }
      )
    }

  }

  private def dbJsConditionAttributesForCondition(collId: Int, exId: Int, taskId: Int, condId: Int): Future[Seq[DbJsConditionAttribute]] = db.run(
    jsConditionAttributesTableQuery
      .filter { ca => ca.collectionId === collId && ca.exerciseId === exId && ca.taskId === taskId && ca.condId === condId }
      .result
  )

  override def futureSampleSolutionsForExPart(collId: Int, exerciseId: Int, part: WebExPart): Future[Seq[String]] = db.run(
    sampleSolutionsTableQuery
      .filter { s => s.exerciseId === exerciseId && s.collectionId === collId }
      .map(sample => part match {
        case WebExParts.HtmlPart => sample.htmlSample
        case WebExParts.JsPart   => sample.jsSample.getOrElse("")
      })
      .result
  )

  // Saving

  override def saveExerciseRest(collId: Int, ex: WebExercise): Future[Boolean] = {
    val dbSamples = ex.sampleSolutions map (s => solutionDbModels.dbSampleSolFromSampleSol(ex.id, ex.semanticVersion, collId, s))


    for {
      htmlTasksSaved <- saveSeq[HtmlTask](ex.siteSpec.htmlTasks, t => saveHtmlTask(ex.id, ex.semanticVersion, collId, t))
      jsTasksSaved <- saveSeq[JsTask](ex.siteSpec.jsTasks, t => saveJsTask(ex.id, ex.semanticVersion, collId, t))

      sampleSolutionsSaved <- saveSeq[DbWebSampleSolution](dbSamples, s => db.run(sampleSolutionsTableQuery += s))
    } yield htmlTasksSaved && jsTasksSaved && sampleSolutionsSaved
  }

  private def saveHtmlTask(exId: Int, exSemVer: SemanticVersion, collId: Int, htmlTask: HtmlTask): Future[Boolean] = {
    val (dbHtmlTask, dbHtmlAttributes) = dbModels.dbHtmlTaskFromHtmlTask(exId, exSemVer, collId, htmlTask)

    db.run(htmlTasksTable += dbHtmlTask) flatMap { _ =>
      saveSeq[DbHtmlAttribute](dbHtmlAttributes, a => db.run(attributesTable += a))
    }
  }

  private def saveJsTask(exId: Int, exSemVer: SemanticVersion, collId: Int, jsTask: JsTask): Future[Boolean] = {
    val (dbJsTask, dbJsConditionsAndAttributes) = dbModels.dbJsTaskFromJsTask(exId, exSemVer, collId, jsTask)

    db.run(jsTasksTable += dbJsTask) flatMap { _ =>
      saveSeq[(DbJsCondition, Seq[DbJsConditionAttribute])](dbJsConditionsAndAttributes, saveJsCondition(dbJsTask.id, exId, exSemVer, collId, _))
    }
  }

  private def saveJsCondition(taskId: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, toSave: (DbJsCondition, Seq[DbJsConditionAttribute])): Future[Boolean] = {
    db.run(jsConditionsTableQuery += toSave._1) flatMap {
      _ => saveSeq[DbJsConditionAttribute](toSave._2, jca => db.run(jsConditionAttributesTableQuery += jca))
    }
  }

}
