package model.persistence

import model.points.Points
import model.{ExPart, SemanticVersion, StringSampleSolution, StringUserSolution}

class StringSolutionDbModels[PartType <: ExPart]
  extends ASolutionDbModels[String, PartType, StringSampleSolution, DbStringSampleSolution, StringUserSolution[PartType], DbStringUserSolution[PartType]] {

  def dbSampleSolFromSampleSol(exId: Int, exSemVer: SemanticVersion, collId: Int, sample: StringSampleSolution): DbStringSampleSolution =
    DbStringSampleSolution(sample.id, exId, exSemVer, collId, sample.sample)

  def sampleSolFromDbSampleSol(dbSample: DbStringSampleSolution): StringSampleSolution =
    StringSampleSolution(dbSample.id, dbSample.sample)


  def dbUserSolFromUserSol(exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, solution: StringUserSolution[PartType]): DbStringUserSolution[PartType] =
    DbStringUserSolution[PartType](solution.id, exId, exSemVer, collId, username, solution.part, solution.solution, solution.points, solution.maxPoints)

  def userSolFromDbUserSol(dbSolution: DbStringUserSolution[PartType]): StringUserSolution[PartType] =
    StringUserSolution[PartType](dbSolution.id, dbSolution.part, dbSolution.solution, dbSolution.points, dbSolution.maxPoints)

}

final case class DbStringSampleSolution(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, sample: String) extends ADbSampleSol[String]

final case class DbStringUserSolution[PartType <: ExPart](id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, username: String,
                                                          part: PartType, solution: String, points: Points, maxPoints: Points) extends ADbUserSol[PartType, String]

object DbStringUserSolution {

  def unapplyWithPart[PartType <: ExPart](x: DbStringUserSolution[PartType]): Option[(Int, Int, SemanticVersion, Int, String, PartType, String, Points, Points)] = Some(
    (x.id, x.exId, x.exSemVer, x.collId, x.username, x.part, x.solution, x.points, x.maxPoints)
  )


  def tupledWithPart[PartType <: ExPart](x: (Int, Int, SemanticVersion, Int, String, PartType, String, Points, Points)): DbStringUserSolution[PartType] = x match {
    case (id, exId, exSemVer, collId, username, part, solution, points, maxPoints) =>
      DbStringUserSolution[PartType](id, exId, exSemVer, collId, username, part, solution, points, maxPoints)
  }


}
