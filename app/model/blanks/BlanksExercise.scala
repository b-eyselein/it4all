package model.blanks

import model.Enums.ExerciseState
import model.core.CompleteEx
import model.core.StringConsts._
import model.core.result.SuccessType
import model.{Exercise, TableDefs}
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Reads}
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

object BlanksExerciseReads {
  implicit def blanksExReads: Reads[BlanksExercise] = (
    (JsPath \ ID_NAME).read[Int] and
      (JsPath \ TITLE_NAME).read[String] and
      (JsPath \ AUTHOR_NAME).read[String] and
      (JsPath \ TEXT_NAME).read[List[String]] and
      (JsPath \ STATE_NAME).read[String]
    ) ((i, ti, a, te, s) => BlanksExercise(i, ti, a, te.mkString, ExerciseState.valueOf(s)))
}

case class BlanksCompleteEx(ex: BlanksExercise) extends CompleteEx[BlanksExercise]

case class BlanksExercise(i: Int, ti: String, a: String, te: String, s: ExerciseState) extends Exercise(i, ti, a, te, s) {

  val objects: List[BlankObject] = List(
    BlankObject(1,
      """&lt?xml version="1.0" encoding="UTF-8"?&gt"
        |&lt!DOCTYPE root SYSTEM "doctype.dtd"&gt
        |&lt""".stripMargin, 4, "&gt", "root"),
    BlankObject(2, "&lt", 5, "&gt", "/root")
  )

  def correct(inputs: List[String]): List[SuccessType] = for {(inp, obj) <- inputs zip objects}
    yield if (inp == obj.solution) SuccessType.COMPLETE else SuccessType.NONE

  def render = new Html(objects.map(_.render).mkString("\n"))

}


trait BlanksExercises extends TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  val blanksExercises = TableQuery[BlanksExercisesTable]

  class BlanksExercisesTable(tag: Tag) extends HasBaseValuesTable[BlanksExercise](tag, "blanks_exercises") {

    def * = (id, title, author, text, state) <> (BlanksExercise.tupled, BlanksExercise.unapply)
  }

}


case class BlankObject(id: Int, preText: String, length: Int, postText: String, solution: String) {

  def render: String =
    s"""$preText
       |<div class="form-group">
       |  <input name="inp$id" id="inp$id"  class="form-control" type="text" size="$length" maxlength="$length">
       |</div>"
       |$postText""".stripMargin

}

