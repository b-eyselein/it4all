package model.spread

import javax.inject.Inject
import model.persistence.FileExesTableDefs
import model.{ExerciseState, SemanticVersion, _}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}

// Classes for use

case class SpreadExercise(id: Int, title: String, author: String, text: String, state: ExerciseState, semanticVersion: SemanticVersion,
                          sampleFilename: String, templateFilename: String) extends Exercise with FileCompleteEx[SpreadExercise, SpreadExPart] {

  override def ex: SpreadExercise = this

  override def preview: Html = // FIXME: move to toolMain!
    views.html.fileExercises.spread.spreadPreview.render(this)

  override def hasPart(partType: SpreadExPart): Boolean = true

}

class SpreadTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[slick.jdbc.JdbcProfile] with FileExesTableDefs[SpreadExercise, SpreadExercise] {

  import profile.api._

  // Abstract types and members

  override protected type ExTableDef = SpreadExerciseTable

  override protected val exTable = TableQuery[SpreadExerciseTable]

  // Queries

  override def completeExForEx(ex: SpreadExercise): Future[SpreadExercise] = Future(ex)

  override def saveExerciseRest(compEx: SpreadExercise): Future[Boolean] = Future(true)

  // Tables

  class SpreadExerciseTable(tag: Tag) extends HasBaseValuesTable[SpreadExercise](tag, "spread_exercises") {

    def sampleFilename = column[String]("sample_filename")

    def templateFilename = column[String]("template_filename")

    override def * = (id, title, author, text, state, semanticVersion, sampleFilename, templateFilename).mapTo[SpreadExercise]

  }

}