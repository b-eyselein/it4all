package model.tools.sql

import model.MyYamlProtocol._
import model._
import model.tools.sql.SqlConsts._
import net.jcazevedo.moultingyaml._
import play.api.Logger

import scala.util.Try

object SqlYamlProtocol extends MyYamlProtocol {

  private val logger = Logger("model.tools.sql.SqlYamlProtocol")

  //  implicit object SqlScenarioYamlFormat extends MyYamlObjectFormat[SqlCompleteScenario] {
  //
  //    override protected def readObject(yamlObject: YamlObject): Try[SqlCompleteScenario] = for {
  //      baseValues <- readBaseValues(yamlObject)
  //
  //      shortName <- yamlObject.stringField(shortNameName)
  //      exercises <- yamlObject.arrayField(exercisesName, SqlExYamlFormat(baseValues).read)
  //    } yield {
  //      for (exFailure <- exercises._2)
  //      // FIXME: return...
  //        logger.error("Could not read sql exercise: ", exFailure.exception)
  //
  //      SqlCompleteScenario(SqlScenario(baseValues.id, baseValues.semanticVersion, baseValues.title, baseValues.author, baseValues.text, baseValues.state, shortName), exercises._1)
  //    }
  //
  //    override def write(sqlCompleteScenario: SqlCompleteScenario): YamlObject = YamlObject(
  //      writeBaseValues(sqlCompleteScenario.coll.baseValues) ++
  //        Map(
  //          YamlString(shortNameName) -> YamlString(sqlCompleteScenario.coll.shortName),
  //          YamlString(exercisesName) -> YamlArr(sqlCompleteScenario.exercises map SqlExYamlFormat(sqlCompleteScenario.coll.baseValues).write)
  //        )
  //    )
  //  }

  //  final case class SqlExYamlFormat(collBaseValues: BaseValues) extends MyYamlObjectFormat[SqlExercise] {
  //
  //    override protected def readObject(yamlObject: YamlObject): Try[SqlExercise] = for {
  //      baseValues <- readBaseValues(yamlObject)
  //
  //      exerciseType <- yamlObject.enumField(exerciseTypeName, SqlExerciseType.withNameInsensitiveOption) map (_ getOrElse SqlExerciseType.SELECT)
  //      tagTries <- yamlObject.optArrayField(tagsName, _.asStringEnum(SqlExTag.withNameInsensitiveOption(_) getOrElse SqlExTag.SQL_JOIN))
  //
  //      hint <- yamlObject.optStringField(hintName)
  //
  //      sampleTries <- yamlObject.arrayField(samplesName, SqlSampleYamlFormat(collBaseValues.id, collBaseValues.semanticVersion, baseValues).read)
  //    } yield {
  //
  //      for (tagFailures <- tagTries._2)
  //      // FIXME: return...
  //        logger.error("Could not read sql tag", tagFailures.exception)
  //
  //      for (sampleFailure <- sampleTries._2)
  //      // FIXME: return...
  //        logger.error("Could not read sql sample", sampleFailure.exception)
  //
  //      SqlExercise(baseValues.id, baseValues.semanticVersion, baseValues.title, baseValues.author, baseValues.text, baseValues.state,
  //        collBaseValues.id, collBaseValues.semanticVersion, exerciseType, tagTries._1, hint, sampleTries._1)
  //    }
  //
  //    override def write(sqlEx: SqlExercise): YamlValue = YamlObject(
  //      writeBaseValues(sqlEx.baseValues) ++
  //        Map[YamlValue, YamlValue](
  //          YamlString(exerciseTypeName) -> YamlString(sqlEx.exerciseType.entryName),
  //          YamlString(samplesName) -> YamlArr(sqlEx.samples map SqlSampleYamlFormat(collBaseValues.id, collBaseValues.semanticVersion, sqlEx.baseValues).write)
  //        ) ++ sqlEx.hint.map(h => YamlString(hintName) -> YamlString(h)) ++ writeTags(sqlEx)
  //    )
  //
  //    private def writeTags(sqlEx: SqlExercise): Option[(YamlValue, YamlValue)] = sqlEx.tags match {
  //      case Nil  => None
  //      case tags => Some(YamlString(tagsName) -> YamlArr(tags.map(t => YamlString(t.entryName))))
  //    }
  //
  //  }


  final case class SqlSampleYamlFormat(collId: Int, exerciseBaseValues: BaseValues) extends MyYamlObjectFormat[SqlSample] {

    override def readObject(yamlObject: YamlObject): Try[SqlSample] = for {
      id <- yamlObject.intField(idName)
      sample <- yamlObject.stringField(sampleName)
    } yield SqlSample(id, exerciseBaseValues.id, exerciseBaseValues.semanticVersion, collId, sample)

    override def write(obj: SqlSample): YamlValue = YamlObj(
      idName -> obj.id,
      sampleName -> obj.sample
    )

  }

}
