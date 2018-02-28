package model.mindmap

import javax.inject.Inject
import model.Enums.ExerciseState
import model._
import model.persistence.ExerciseTableDefs
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

// Wrapper classes

class MindmapCompleteExWrapper(override val compEx: MindmapExercise) extends CompleteExWrapper {

  override type Ex = MindmapExercise

  override type CompEx = MindmapExercise

}

// Classes for use

case class MindmapExercise(override val id: Int, override val title: String, override val author: String, override val text: String, override val state: ExerciseState)
  extends Exercise with FileCompleteEx[MindmapExercise, MindmapExPart] {

  override val ex: MindmapExercise = this

  override def preview: Html = ???

  override def templateFilename: String = ???

  override def sampleFilename: String = ???

  override def wrapped: CompleteExWrapper = ???

  override def hasPart(partType: MindmapExPart): Boolean = true

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