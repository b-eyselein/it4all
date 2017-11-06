package model.mindmap

import model.Enums.ExerciseState
import model.core.StringConsts._
import model.{DbExercise, TableDefs}
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Reads}
import slick.jdbc.JdbcProfile

object MindmapExerciseReads {
  implicit def mindmapExReads: Reads[MindmapExercise] = (
    (JsPath \ ID_NAME).read[Int] and
      (JsPath \ TITLE_NAME).read[String] and
      (JsPath \ AUTHOR_NAME).read[String] and
      (JsPath \ TEXT_NAME).read[List[String]] and
      (JsPath \ STATE_NAME).read[String]
    ) ((i, ti, a, te, s) => MindmapExercise(i, ti, a, te.mkString, ExerciseState.valueOf(s)))
}

case class MindmapExercise(i: Int, ti: String, a: String, te: String, s: ExerciseState) extends DbExercise(i, ti, a, te, s)


trait MindmapExercises extends TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  val mindmapExercises = TableQuery[MindmapExercisesTable]

  class MindmapExercisesTable(tag: Tag) extends HasBaseValuesTable[MindmapExercise](tag, "mindmap_exercises") {

    def * = (id, title, author, text, state) <> (MindmapExercise.tupled, MindmapExercise.unapply)
  }

}