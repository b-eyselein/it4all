package model.blanks

import controllers.exes.idExes.BlanksToolObject
import model.Enums.{ExerciseState, SuccessType}
import model.{BaseValues, CompleteEx, Exercise, TableDefs}
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.mvc.Call
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

case class BlanksExercise(i: Int, ti: String, a: String, te: String, s: ExerciseState) extends Exercise with CompleteEx[BlanksExercise] {

  override val baseValues = BaseValues(i, ti, a, te, s)

  override val ex: BlanksExercise = this

  val objects: List[BlankObject] = List(
    BlankObject(1,
      """&lt?xml version="1.0" encoding="UTF-8"?&gt"
        |&lt!DOCTYPE root SYSTEM "doctype.dtd"&gt
        |&lt""".stripMargin, 4, "&gt", "root"),
    BlankObject(2, "&lt", 5, "&gt", "/root")
  )

  def correct(inputs: List[String]): List[SuccessType] = for {(inp, obj) <- inputs zip objects}
    yield if (inp == obj.solution) SuccessType.COMPLETE else SuccessType.NONE

  def render = new Html(objects.map(_.render) mkString "\n")

  override def preview: Html = ???

  override def exerciseRoutes: Map[Call, String] = BlanksToolObject.exerciseRoutes(this)
}

trait BlanksTableDefs extends TableDefs {
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

