package model.mindmap

import model.Enums.ExerciseState
import model.{BaseValues, CompleteEx, Exercise, TableDefs}
import play.api.db.slick.HasDatabaseConfigProvider
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

case class MindmapExercise(i: Int, ti: String, a: String, te: String, s: ExerciseState) extends Exercise(BaseValues(i, ti, a, te, s)) with CompleteEx[MindmapExercise] {

  override val ex: MindmapExercise = this

//  override def preview: Html = ???

}


trait MindmapTableDefs extends TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  val mindmapExercises = TableQuery[MindmapExercisesTable]

  class MindmapExercisesTable(tag: Tag) extends HasBaseValuesTable[MindmapExercise](tag, "mindmap_exercises") {

    def * = (id, title, author, text, state) <> (MindmapExercise.tupled, MindmapExercise.unapply)
  }

}