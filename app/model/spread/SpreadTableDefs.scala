package model.spread

import javax.inject.Inject
import model.persistence.IdExerciseTableDefs
import model.{Exercise, ExerciseState, FileCompleteEx, SemanticVersion}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.twirl.api.Html
import slick.lifted.ProvenShape

import scala.concurrent.{ExecutionContext, Future}

// Classes for use

final case class SpreadExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                          sampleFilename: String, templateFilename: String) extends Exercise with FileCompleteEx[SpreadExercise, SpreadExPart] {

  override def ex: SpreadExercise = this

  override def preview: Html = // FIXME: move to toolMain!
    views.html.fileExercises.spread.spreadPreview.render(this)

  override def hasPart(partType: SpreadExPart): Boolean = true

}

class SpreadTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[slick.jdbc.JdbcProfile] with IdExerciseTableDefs[SpreadExercise, SpreadExercise] {

  import profile.api._

  // Abstract types and members

  override protected type ExTableDef = SpreadExerciseTable

  override protected val exTable = TableQuery[SpreadExerciseTable]

  // Queries

  override def completeExForEx(ex: SpreadExercise): Future[SpreadExercise] = Future(ex)

  override def saveExerciseRest(compEx: SpreadExercise): Future[Boolean] = Future(true)

  // Tables

  class SpreadExerciseTable(tag: Tag) extends ExerciseTableDef(tag, "spread_exercises") {

    def sampleFilename: Rep[String] = column[String]("sample_filename")

    def templateFilename: Rep[String] = column[String]("template_filename")


    override def * : ProvenShape[SpreadExercise] = (id, semanticVersion, title, author, text, state, sampleFilename, templateFilename) <> (SpreadExercise.tupled, SpreadExercise.unapply)

  }

}