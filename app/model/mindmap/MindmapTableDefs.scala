package model.mindmap

import javax.inject.Inject
import model.Enums.ExerciseState
import model._
import model.persistence.FileExesTableDefs
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

// Classes for use

case class MindmapExercise(override val id: Int, override val title: String, override val author: String, override val text: String, override val state: ExerciseState)
  extends Exercise with FileCompleteEx[MindmapExercise, MindmapExPart] {

  override val ex: MindmapExercise = this

  override def preview: Html = ???

  override def templateFilename: String = ???

  override def sampleFilename: String = ???

  override def hasPart(partType: MindmapExPart): Boolean = true

}

class MindmapTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] with FileExesTableDefs[MindmapExercise, MindmapExercise] {

  import profile.api._

  // Abstract types and members

  override protected type ExTableDef = MindmapExercisesTable

  override protected val exTable = TableQuery[MindmapExercisesTable]

  // Queries

  override def completeExForEx(ex: MindmapExercise)(implicit ec: ExecutionContext): Future[MindmapExercise] = Future(ex)

  override def saveExerciseRest(compEx: MindmapExercise)(implicit ec: ExecutionContext): Future[Boolean] = Future(true)

  // Table Defs

  class MindmapExercisesTable(tag: Tag) extends HasBaseValuesTable[MindmapExercise](tag, "mindmap_exercises") {

    def * = (id, title, author, text, state) <> (MindmapExercise.tupled, MindmapExercise.unapply)

  }

}