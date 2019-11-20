package model.tools.collectionTools.xml.persistence

import model.SemanticVersion
import model.tools.collectionTools.xml.{XmlExPart, XmlExercise, XmlSampleSolution, XmlUserSolution}

import scala.concurrent.Future

trait XmlTableQueries {
  self: XmlTableDefs =>

  import profile.api._

  override protected def completeExForEx(collId: Int, ex: DbXmlExercise): Future[XmlExercise] = for {
    samples <- db.run(sampleSolutionsTableQuery.filter { e => e.exerciseId === ex.id && e.collectionId === collId && e.exSemVer === ex.semanticVersion }.result)
      .map(_ map XmlSolutionDbModels.sampleSolFromDbSampleSol)
  } yield XmlDbModels.exerciseFromDbValues(ex, samples)

  override def futureSampleSolutionsForExPart(collId: Int, exId: Int, part: XmlExPart): Future[Seq[XmlSampleSolution]] = db.run(
    sampleSolutionsTableQuery
      .filter { s => s.collectionId === collId && s.exerciseId === exId }
      .result)
    .map(_.map(XmlSolutionDbModels.sampleSolFromDbSampleSol))

  //    .map { sampleSol =>
  //      part match {
  //        case XmlExParts.GrammarCreationXmlPart  => sampleSol.grammar
  //        case XmlExParts.DocumentCreationXmlPart => sampleSol.document
  //      }
  //    }

  override def saveExerciseRest(collId: Int, compEx: XmlExercise): Future[Boolean] = {
    val dbSamples = compEx.sampleSolutions map (s => XmlSolutionDbModels.dbSampleSolFromSampleSol(compEx.id, compEx.semanticVersion, collId, s))

    for {
      samplesSaved <- saveSeq[DbXmlSampleSolution](dbSamples, i => db.run(sampleSolutionsTableQuery += i), Some("XmlSample"))
    } yield samplesSaved
  }

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
