package model.tools.xml.persistence

import model.SemanticVersion
import model.tools.xml.{XmlExPart, XmlExParts, XmlExercise, XmlUserSolution}

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait XmlTableQueries {
  self: XmlTableDefs =>

  import profile.api._

  override protected def completeExForEx(collId: Int, ex: DbXmlExercise): Future[XmlExercise] = for {
    samples <- db.run(sampleSolutionsTableQuery.filter { e => e.exerciseId === ex.id && e.collectionId === collId && e.exSemVer === ex.semanticVersion }.result)
      .map(_ map XmlSolutionDbModels.sampleSolFromDbSampleSol)
  } yield dbModels.exerciseFromDbValues(ex, samples)

  override def futureSampleSolutionsForExPart(collId: Int, exId: Int, part: XmlExPart): Future[Seq[String]] = db.run(
    sampleSolutionsTableQuery
      .filter { s => s.collectionId === collId && s.exerciseId === exId }
      .map { sampleSol =>
        part match {
          case XmlExParts.GrammarCreationXmlPart  => sampleSol.grammar
          case XmlExParts.DocumentCreationXmlPart => sampleSol.document
        }
      }
      .result)

  override def saveExerciseRest(collId: Int, compEx: XmlExercise): Future[Boolean] = {
    val dbSamples = compEx.samples map (s => XmlSolutionDbModels.dbSampleSolFromSampleSol(compEx.id, compEx.semanticVersion, collId, s))

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

  def futureSaveUserSolution(exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, sol: XmlUserSolution): Future[Boolean] = {
    val dbUserSol = XmlSolutionDbModels.dbUserSolFromUserSol(exId, exSemVer, collId, username, sol)

    val insertQuery = userSolutionsTableQuery returning userSolutionsTableQuery.map(_.id) into ((dbUserSol, id) => copyDbUserSolType(dbUserSol, id))

    db.run(insertQuery += dbUserSol) transform {
      case Success(_) => Success(true)
      case Failure(e) =>
        logger.error("Could not save solution", e)
        Success(false)
    }
  }

}
