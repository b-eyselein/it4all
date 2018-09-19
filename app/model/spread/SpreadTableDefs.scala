package model.spread

import javax.inject.Inject
import model.persistence.IdExerciseTableDefs
import model.{Difficulty, Exercise, ExerciseReview, ExerciseState, FileCompleteEx, SemanticVersion}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.twirl.api.Html
import slick.jdbc.JdbcProfile
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

final case class SpreadExerciseReview(username: String, exerciseId: Int, exerciseSemVer: SemanticVersion, exercisePart: SpreadExPart,
                                      difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview[SpreadExPart]

class SpreadTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with IdExerciseTableDefs[SpreadExercise, SpreadExercise, SpreadExPart, SpreadExerciseReview] {

  import profile.api._

  // Abstract types and members

  override protected type ExTableDef = SpreadExerciseTable
  override protected type ReviewsTableDef = SpreadExerciseReviewsTable

  override protected val exTable      = TableQuery[SpreadExerciseTable]
  override protected val reviewsTable = TableQuery[SpreadExerciseReviewsTable]

  // Queries

  override def completeExForEx(ex: SpreadExercise): Future[SpreadExercise] = Future(ex)

  override def saveExerciseRest(compEx: SpreadExercise): Future[Boolean] = Future(true)

  // Column type

  override implicit val partTypeColumnType: BaseColumnType[SpreadExPart] =
    MappedColumnType.base[SpreadExPart, String](_.entryName, SpreadExParts.withNameInsensitive)

  // Tables

  class SpreadExerciseTable(tag: Tag) extends ExerciseTableDef(tag, "spread_exercises") {

    def sampleFilename: Rep[String] = column[String]("sample_filename")

    def templateFilename: Rep[String] = column[String]("template_filename")


    override def * : ProvenShape[SpreadExercise] = (id, semanticVersion, title, author, text, state, sampleFilename, templateFilename) <> (SpreadExercise.tupled, SpreadExercise.unapply)

  }

  class SpreadExerciseReviewsTable(tag: Tag) extends ExerciseReviewsTable(tag, "spread_exercise_reviews") {

    override def * : ProvenShape[SpreadExerciseReview] = (username, exerciseId, exerciseSemVer, exercisePart, difficulty, maybeDuration.?) <> (SpreadExerciseReview.tupled, SpreadExerciseReview.unapply)

  }

}