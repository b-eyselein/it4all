package model.sql

import model.{MyYamlProtocol, SemanticVersion}
import model.MyYamlProtocol._
import model.sql.SqlConsts._
import model.sql.SqlYamlProtocol.{SqlSampleYamlFormat, logger, readBaseValues}
import net.jcazevedo.moultingyaml.{YamlObject, YamlValue}
import play.api.Logger

import scala.util.Try

object NewSqlYamlProtocol extends MyYamlProtocol {

  private val logger = Logger(NewSqlYamlProtocol.getClass)

  object SqlCollectionYamlFormat extends MyYamlObjectFormat[SqlScenario] {

    override protected def readObject(yamlObject: YamlObject): Try[SqlScenario] = for {
      baseValues <- readBaseValues(yamlObject)

      shortName <- yamlObject.stringField(shortNameName)
    } yield SqlScenario(baseValues.id, baseValues.semanticVersion, baseValues.title, baseValues.author, baseValues.text, baseValues.state, shortName)

    override def write(obj: SqlScenario): YamlValue = ???

  }

  final case class SqlExerciseYamlFormat(collId: Int, collSemVer: SemanticVersion) extends MyYamlObjectFormat[SqlExercise] {

    //    override protected def readObject(yamlObject: YamlObject): Try[SqlExercise] = ???

    override def write(obj: SqlExercise): YamlValue = ???


    override protected def readObject(yamlObject: YamlObject): Try[SqlExercise] = for {
      baseValues <- readBaseValues(yamlObject)

      exerciseType <- yamlObject.enumField(exerciseTypeName, SqlExerciseType.withNameInsensitiveOption) map (_ getOrElse SqlExerciseType.SELECT)
      tagTries <- yamlObject.optArrayField(tagsName, _.asStringEnum(SqlExTag.withNameInsensitiveOption(_) getOrElse SqlExTag.SQL_JOIN))

      hint <- yamlObject.optStringField(hintName)

      sampleTries <- yamlObject.arrayField(samplesName, SqlSampleYamlFormat(collId, collSemVer, baseValues).read)
    } yield {

      for (tagFailures <- tagTries._2)
      // FIXME: return...
        logger.error("Could not read sql tag", tagFailures.exception)

      //      for (sampleFailure <- sampleTries._2)
      // // FIXME: return...
      //        logger.error("Could not read sql sample", sampleFailure.exception)

      SqlExercise(baseValues.id, baseValues.semanticVersion, baseValues.title, baseValues.author, baseValues.text, baseValues.state,
        collId, collSemVer, exerciseType, tagTries._1, hint, sampleTries._1)
    }
  }

}
