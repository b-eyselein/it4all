package model.mindmap

import javax.inject.Inject

import controllers.exes.fileExes.MindMapToolObject
import model.Enums.ExerciseState
import model._
import model.core.tools.FileExToolObject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.Call
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case class MindmapExercise(i: Int, ti: String, a: String, te: String, s: ExerciseState) extends Exercise with FileCompleteEx[MindmapExercise] {

  override val toolObject: FileExToolObject = MindMapToolObject

  override val baseValues: BaseValues = BaseValues(i, ti, a, te, s)

  override val ex: MindmapExercise = this

  override def preview: Html = ???

  override def templateFilename: String = ???

  override def sampleFilename: String = ???

  override def exerciseRoutes: Map[Call, String] = MindMapToolObject.exerciseRoutes(this)

}


class MindmapTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] with ExerciseTableDefs[MindmapExercise, MindmapExercise] {

  import profile.api._

  val mindmapExercises = TableQuery[MindmapExercisesTable]

  override type ExTableDef = MindmapExercisesTable

  override val exTable = mindmapExercises

  override def completeExForEx(ex: MindmapExercise)(implicit ec: ExecutionContext): Future[MindmapExercise] = Future(ex)

  override def saveExerciseRest(compEx: MindmapExercise)(implicit ec: ExecutionContext): Future[Boolean] = Future(true)

  // Table Defs

  class MindmapExercisesTable(tag: Tag) extends HasBaseValuesTable[MindmapExercise](tag, "mindmap_exercises") {

    def * = (id, title, author, text, state) <> (MindmapExercise.tupled, MindmapExercise.unapply)

  }

}