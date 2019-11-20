package model.tools.collectionTools.xml.persistence

import model.SemanticVersion
import model.tools.collectionTools.xml.{XmlExPart, XmlExercise, XmlUserSolution}

import scala.concurrent.Future

trait XmlTableQueries {
  self: XmlTableDefs =>

  import profile.api._

  override protected def completeExForEx(collId: Int, ex: XmlExercise): Future[XmlExercise] = Future.successful(ex)

  override def saveExerciseRest(collId: Int, compEx: XmlExercise): Future[Boolean] = Future.successful(true)

  def futureMaybeOldSolution(username: String, collId: Int, exId: Int, part: XmlExPart): Future[Option[XmlUserSolution]] = db.run(
    userSolutionsTableQuery
      .filter { sol => sol.username === username && sol.collectionId === collId && sol.exerciseId === exId && sol.part === part }
      .sortBy(_.id.desc) // take last sample sol (with highest id)
      .result
      .headOption
      .map(_ map XmlSolutionDbModels.userSolFromDbUserSol))

  def futureSaveUserSolution(exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, sol: XmlUserSolution): Future[Boolean] =
    nextUserSolutionId(exId, collId, username, sol.part).flatMap { nextUserSolId =>
      val dbUserSol = XmlSolutionDbModels.dbUserSolFromUserSol(exId, exSemVer, collId, username, sol).copy(id = nextUserSolId)
      db.run(userSolutionsTableQuery += dbUserSol).transform(_ == 1, identity)
    }

}
