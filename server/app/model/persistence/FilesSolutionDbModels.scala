package model.persistence

import model._
import model.points.Points
import model.tools.collectionTools.{ExPart, ExerciseFile}


trait DbFile {
  val name    : String
  val content : String
  val fileType: String
  val editable: Boolean

  def toExerciseFile: ExerciseFile = ExerciseFile(name, content, fileType, editable)
}


final case class DbFilesUserSolution[PartType <: ExPart](id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, part: PartType,
                                                         username: String, points: Points, maxPoints: Points)
  extends ADbUserSol[PartType]

object DbFilesUserSolution {

  def tupledWithType[PartType <: ExPart](x: (Int, Int, SemanticVersion, Int, String, PartType, Points, Points)): DbFilesUserSolution[PartType] = x match {
    case (id, exerciseId, exSemVer, collectionId, username, part, points, maxPoints) =>
      DbFilesUserSolution(id, exerciseId, exSemVer, collectionId, part, username, points, maxPoints)
  }

  def unapplyWithType[PartType <: ExPart](x: DbFilesUserSolution[PartType]): Option[(Int, Int, SemanticVersion, Int, String, PartType, Points, Points)] =
    Some((x.id, x.exId, x.exSemVer, x.collId, x.username, x.part, x.points, x.maxPoints))

}


final case class DbFilesUserSolutionFile[PartType <: ExPart](name: String, solId: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, username: String,
                                                             part: PartType, content: String, fileType: String, editable: Boolean) extends DbFile

object DbFilesUserSolutionFile {

  def tupledWithType[PartType <: ExPart](x: (String, Int, Int, SemanticVersion, Int, String, PartType, String, String, Boolean)): DbFilesUserSolutionFile[PartType] = x match {
    case (name, solId, exId, exSemVer, collId, part, username, content, fileType, editable) =>
      DbFilesUserSolutionFile[PartType](name, solId, exId, exSemVer, collId, part, username, content, fileType, editable)
  }

  def unapplyWithType[PartType <: ExPart](x: DbFilesUserSolutionFile[PartType]): Option[(String, Int, Int, SemanticVersion, Int, String, PartType, String, String, Boolean)] =
    Some((x.name, x.solId, x.exId, x.exSemVer, x.collId, x.username, x.part, x.content, x.fileType, x.editable))

}


object FilesSolutionDbModels {

  // User solution

  private def dbFilesUserSolutionFileFromExerciseFile[PartType <: ExPart](solId: Int, exId: Int, exSemVer: SemanticVersion, collId: Int,
                                                                          username: String, part: PartType, exFile: ExerciseFile): DbFilesUserSolutionFile[PartType] =
    DbFilesUserSolutionFile(exFile.name, solId, exId, exSemVer, collId, username, part, exFile.content, exFile.fileType, exFile.editable)

  def dbUserSolFromUserSol[PartType <: ExPart](exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, solution: FilesUserSolution[PartType]):
  (DbFilesUserSolution[PartType], Seq[DbFilesUserSolutionFile[PartType]]) = {
    val dbSolution      = DbFilesUserSolution(solution.id, exId, exSemVer, collId, solution.part, username, solution.points, solution.maxPoints)
    val dbSolutionFiles = solution.solution.map(dbFilesUserSolutionFileFromExerciseFile(solution.id, exId, exSemVer, collId, username, solution.part, _))

    (dbSolution, dbSolutionFiles)
  }

  def userSolFromDbUserSol[PartType <: ExPart](dbSolution: DbFilesUserSolution[PartType], files: Seq[DbFilesUserSolutionFile[PartType]]): FilesUserSolution[PartType] =
    FilesUserSolution[PartType](dbSolution.id, dbSolution.part, files.map(_.toExerciseFile), dbSolution.points, dbSolution.maxPoints)

}
