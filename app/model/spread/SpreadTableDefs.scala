package model.spread

import javax.inject.Inject
import model.Enums.ExerciseState
import model._
import model.persistence.FileExesTableDefs
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}

// Classes for use

case class SpreadExercise(override val id: Int, override val title: String, override val author: String, override val text: String, override val state: ExerciseState,
                          sampleFilename: String, templateFilename: String)
  extends Exercise with FileCompleteEx[SpreadExercise, SpreadExPart] {

  def this(baseValues: (Int, String, String, String, ExerciseState), sampleFileName: String, templateFileName: String) =
    this(baseValues._1, baseValues._2, baseValues._3, baseValues._4, baseValues._5, sampleFileName, templateFileName)

  override def ex: SpreadExercise = this

  override def preview: Html = views.html.spread.spreadPreview.render(this)

  override def hasPart(partType: SpreadExPart): Boolean = true

}

class SpreadTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[slick.jdbc.JdbcProfile] with FileExesTableDefs[SpreadExercise, SpreadExercise] {

  import profile.api._

  // Abstract types and members

  override protected type ExTableDef = SpreadExerciseTable

  override protected val exTable = TableQuery[SpreadExerciseTable]

  // Queries

  override def completeExForEx(ex: SpreadExercise)(implicit ec: ExecutionContext): Future[SpreadExercise] = Future(ex)

  override def saveExerciseRest(compEx: SpreadExercise)(implicit ec: ExecutionContext): Future[Boolean] = Future(true)

  // Tables

  class SpreadExerciseTable(tag: Tag) extends HasBaseValuesTable[SpreadExercise](tag, "spread_exercises") {

    def sampleFilename = column[String]("sample_filename")

    def templateFilename = column[String]("template_filename")

    override def * = (id, title, author, text, state, sampleFilename, templateFilename) <> (SpreadExercise.tupled, SpreadExercise.unapply)

  }

}