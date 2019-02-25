package model.web.persistence

import model.persistence.DbModels
import model.web.{HtmlCompleteTask, JsCompleteTask, WebExercise, WebSampleSolution}
import model.{ExerciseState, HasBaseValues, SemanticVersion}

object WebDbModels extends DbModels {

  override type Exercise = WebExercise

  override type DbExercise = DbWebExercise

  override def dbExerciseFromExercise(ex: WebExercise): DbWebExercise =
    DbWebExercise(ex.id, ex.semanticVersion, ex.title, ex.author, ex.text, ex.state, ex.htmlText, ex.jsText)

  def exerciseFromDbExercise(ex: DbWebExercise, htmlTasks: Seq[HtmlCompleteTask], jsTasks: Seq[JsCompleteTask], sampleSolutions: Seq[WebSampleSolution]) =
    WebExercise(
      ex.id, ex.semanticVersion, /*collectionId = 1, collSemVer = SemanticVersionHelper.DEFAULT,*/
      ex.title, ex.author, ex.title, ex.state, ex.htmlText, ex.jsText,
      htmlTasks, jsTasks, sampleSolutions
    )

}


final case class DbWebExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                               htmlText: Option[String], jsText: Option[String]) extends HasBaseValues
