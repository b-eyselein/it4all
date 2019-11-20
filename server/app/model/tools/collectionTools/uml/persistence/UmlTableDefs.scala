package model.tools.collectionTools.uml.persistence

import javax.inject.Inject
import model.SemanticVersion
import model.core.CoreConsts.solutionName
import model.persistence.ExerciseTableDefs
import model.tools.collectionTools.ExParts
import model.tools.collectionTools.uml._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.{ExecutionContext, Future}

class UmlTableDefs @Inject()(override protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile]
    with ExerciseTableDefs[UmlExPart, UmlExercise, UmlSampleSolution, UmlUserSolution, UmlExerciseReview] {

  import profile.api._

  // Abstract types

  override protected type DbExType = UmlExercise

  override protected type ExTableDef = UmlExercisesTable

  override protected type CollTableDef = UmlCollectionsTable


  override protected type DbUserSolType = DbUmlUserSolution

  override protected type DbUserSolTable = UmlSolutionsTable


  override protected type DbReviewType = DbUmlExerciseReview

  override protected type ReviewsTable = UmlExerciseReviewsTable

  // Table Queries

  override protected val collTable: TableQuery[UmlCollectionsTable] = TableQuery[UmlCollectionsTable]
  override protected val exTable  : TableQuery[UmlExercisesTable]   = TableQuery[UmlExercisesTable]

  override protected val userSolutionsTableQuery: TableQuery[UmlSolutionsTable] = TableQuery[UmlSolutionsTable]

  override protected val reviewsTable: TableQuery[UmlExerciseReviewsTable] = TableQuery[UmlExerciseReviewsTable]

  // Helper methods

  override protected val exParts: ExParts[UmlExPart] = UmlExParts

  override protected val dbModels               = UmlDbModels
  override protected val exerciseReviewDbModels = UmlExerciseReviewDbModels

  override protected def copyDbUserSolType(oldSol: DbUmlUserSolution, newId: Int): DbUmlUserSolution = oldSol.copy(id = newId)

  // Queries

  override def completeExForEx(collId: Int, ex: UmlExercise): Future[UmlExercise] = Future.successful(ex)

  override protected def saveExerciseRest(collId: Int, compEx: UmlExercise): Future[Boolean] = Future.successful(true)


  override def futureMaybeOldSolution(username: String, scenarioId: Int, exerciseId: Int, part: UmlExPart): Future[Option[UmlUserSolution]] =
    Future.successful(None) // ???

  override def futureSaveUserSolution(exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, sol: UmlUserSolution): Future[Boolean] =
    Future.successful(false) // ???

  // Implicit column types

  override protected implicit val partTypeColumnType: BaseColumnType[UmlExPart] = jsonColumnType(exParts.jsonFormat)

  //  private implicit val umlClassDiagramColumnType: BaseColumnType[UmlClassDiagram] =
  //  // FIXME: refactor!
  //    MappedColumnType.base[UmlClassDiagram, String](UmlClassDiagramJsonFormat.umlSolutionJsonFormat.writes(_).toString(),
  //      str => UmlClassDiagramJsonFormat.umlSolutionJsonFormat.reads(Json.parse(str)) match {
  //        case JsSuccess(sol, _) => sol
  //        case JsError(_)        => ???
  //      })

  // Table definitions

  protected class UmlCollectionsTable(tag: Tag) extends ExerciseCollectionsTable(tag, "uml_collections")

  protected class UmlExercisesTable(tag: Tag) extends ExerciseInCollectionTable(tag, "uml_exercises") {

    private implicit val toct: BaseColumnType[Seq[String]] = stringSeqColumnType

    private implicit val mct: BaseColumnType[Map[String, String]] = stringMapColumnType

    private implicit val ssct: BaseColumnType[Seq[UmlSampleSolution]] =
      jsonSeqColumnType(UmlToolJsonProtocol.sampleSolutionFormat)


    def toIgnore: Rep[Seq[String]] = column[Seq[String]]("to_ignore_json")

    def mappings: Rep[Map[String, String]] = column[Map[String, String]]("mappings_json")

    def sampleSolutions: Rep[Seq[UmlSampleSolution]] = column[Seq[UmlSampleSolution]]("sample_solutions_json")


    override def * : ProvenShape[UmlExercise] = (
      id, collectionId, toolId, semanticVersion,
      title, author, text, state,
      toIgnore, mappings, sampleSolutions
    ) <> (UmlExercise.tupled, UmlExercise.unapply)

  }


  protected class UmlSolutionsTable(tag: Tag) extends AUserSolutionsTable(tag, "uml_user_solutions") {

    private implicit val ucdct: BaseColumnType[UmlClassDiagram] = umlClassDiagramColumnType


    def solution: Rep[UmlClassDiagram] = column[UmlClassDiagram](solutionName)


    override def * : ProvenShape[DbUmlUserSolution] = (id, exerciseId, exSemVer, collectionId, username,
      part, solution, points, maxPoints) <> (DbUmlUserSolution.tupled, DbUmlUserSolution.unapply)

  }

  protected class UmlExerciseReviewsTable(tag: Tag) extends ExerciseReviewsTable(tag, "uml_exercise_reviews") {

    def * : ProvenShape[DbUmlExerciseReview] = (username, collectionId, exerciseId, exercisePart, difficulty, maybeDuration.?) <> (DbUmlExerciseReview.tupled, DbUmlExerciseReview.unapply)

  }

}
