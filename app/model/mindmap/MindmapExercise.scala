package model.mindmap

import controllers.fileExes.{FileExToolObject, MindMapToolObject}
import model.Enums.ExerciseState
import model._
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.mvc.Call
import slick.jdbc.JdbcProfile

case class MindmapExercise(i: Int, ti: String, a: String, te: String, s: ExerciseState) extends Exercise with FileCompleteEx[MindmapExercise] {

  override val toolObject: FileExToolObject = MindMapToolObject

  override val baseValues: BaseValues = BaseValues(i, ti, a, te, s)

  override val ex: MindmapExercise = this

  override def preview = ???

  override def renderListRest = ???

  override def templateFilename: String = ???

  override def sampleFilename: String = ???

  override def exerciseRoutes: Map[Call, String] = MindMapToolObject.exerciseRoutes(this)
}


trait MindmapTableDefs extends TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  val mindmapExercises = TableQuery[MindmapExercisesTable]

  class MindmapExercisesTable(tag: Tag) extends HasBaseValuesTable[MindmapExercise](tag, "mindmap_exercises") {

    def * = (id, title, author, text, state) <> (MindmapExercise.tupled, MindmapExercise.unapply)
  }

}