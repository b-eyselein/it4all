package model.persistence

import model._
import model.core.CoreConsts._
import model.tools.collectionTools.{ExPart, Exercise, ExerciseFile}
import slick.lifted.{ForeignKeyQuery, PrimaryKey, ProvenShape}

trait FilesSolutionExerciseTableDefs[PartType <: ExPart, ExType <: Exercise, ReviewType <: ExerciseReview]
  extends ExerciseTableDefs[PartType, ExType, Seq[ExerciseFile], FilesSampleSolution, FilesUserSolution[PartType], ReviewType]
    with FilesSolutionExerciseTableQueries[PartType, ExType, ReviewType] {

  import profile.api._

  // Abstract types

  override protected type DbUserSolType = DbFilesUserSolution[PartType]

  override protected type DbUserSolTable <: AFilesUserSolutionsTable

  protected type DbFilesUserSolutionFilesTable <: AFilesUserSolutionFilesTable


  // Table Queries

  protected val userSolutionFilesTableQuery: TableQuery[DbFilesUserSolutionFilesTable]


  // Abstract Tables

  abstract class AFilesUserSolutionsTable(tag: Tag, tableName: String) extends AUserSolutionsTable(tag, tableName) {

    override def * : ProvenShape[DbFilesUserSolution[PartType]] = (id, exerciseId, exSemVer, collectionId, username, part,
      points, maxPoints) <> (DbFilesUserSolution.tupledWithType[PartType], DbFilesUserSolution.unapplyWithType[PartType])

  }

  class AFilesUserSolutionFilesTable(tag: Tag, tableName: String) extends Table[DbFilesUserSolutionFile[PartType]](tag, tableName) {

    def name: Rep[String] = column[String](nameName)

    def solId: Rep[Int] = column[Int]("solution_id")

    def exId: Rep[Int] = column[Int]("exercise_id")

    def exSemVer: Rep[SemanticVersion] = column[SemanticVersion]("ex_sem_ver")

    def collId: Rep[Int] = column[Int]("collection_id")

    def part: Rep[PartType] = column[PartType](partName)

    def username: Rep[String] = column[String](usernameName)

    def content: Rep[String] = column[String](contentName)

    def fileType: Rep[String] = column[String]("file_type")

    def editable: Rep[Boolean] = column[Boolean]("editable")


    def pk: PrimaryKey = primaryKey("pk", (name, solId, exId, exSemVer, collId, part, username))

    def solFk: ForeignKeyQuery[DbUserSolTable, DbFilesUserSolution[PartType]] = foreignKey("sol_fk", (solId, exId, exSemVer, collId, part, username),
      userSolutionsTableQuery)(s => (s.id, s.exerciseId, s.exSemVer, s.collectionId, s.part, s.username))


    override def * : ProvenShape[DbFilesUserSolutionFile[PartType]] = (name, solId, exId, exSemVer, collId, username, part, content, fileType,
      editable) <> (DbFilesUserSolutionFile.tupledWithType[PartType], DbFilesUserSolutionFile.unapplyWithType[PartType])

  }

}
