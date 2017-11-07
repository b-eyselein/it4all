package model.spread


import model.Enums.ExerciseState
import model.core.CompleteEx
import model.core.StringConsts._
import model.{Exercise, TableDefs}
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Reads}
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

object SpreadExerciseReads {
  implicit def spreadExReads: Reads[SpreadExercise] = (
    (JsPath \ ID_NAME).read[Int] and
      (JsPath \ TITLE_NAME).read[String] and
      (JsPath \ AUTHOR_NAME).read[String] and
      (JsPath \ TEXT_NAME).read[List[String]] and
      (JsPath \ STATE_NAME).read[String] and
      (JsPath \ "sampleFilename").read[String] and
      (JsPath \ "templateFilename").read[String]
    ) ((i, ti, a, te, s, sa, tem) => SpreadExercise(i, ti, a, te.mkString, ExerciseState.valueOf(s), sa, tem))
}

case class SpreadCompleteEx(ex: SpreadExercise) extends CompleteEx[SpreadExercise]

case class SpreadExercise(i: Int, ti: String, a: String, te: String, es: ExerciseState,
                          sampleFileName: String, templateFilename: String)
  extends Exercise(i, ti, a, te, es) {

  override def renderRest: Html = new Html(s"""<td>$sampleFileName</td><td>$templateFilename</td>""")
}

trait SpreadExercises extends TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  val spreadExercises = TableQuery[SpreadExerciseTable]

  class SpreadExerciseTable(tag: Tag) extends HasBaseValuesTable[SpreadExercise](tag, "spread_exercises") {

    def sampleFilename = column[String]("sample_filename")

    def templateFilename = column[String]("template_filename")

    def * = (id, title, author, text, state, sampleFilename, templateFilename) <> (SpreadExercise.tupled, SpreadExercise.unapply)

  }

}