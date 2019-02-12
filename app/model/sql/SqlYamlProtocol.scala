package model.sql

import model.MyYamlProtocol._
import model._
import model.sql.SqlConsts._
import net.jcazevedo.moultingyaml._
import play.api.Logger

import scala.util.Try

object SqlYamlProtocol extends MyYamlProtocol {

  private val logger = Logger("model.sql.SqlYamlProtocol")

  implicit object SqlScenarioYamlFormat extends MyYamlObjectFormat[SqlCompleteScenario] {

    override protected def readObject(yamlObject: YamlObject): Try[SqlCompleteScenario] = for {
      baseValues <- readBaseValues(yamlObject)

      shortName <- yamlObject.stringField(shortNameName)
      exercises <- yamlObject.arrayField(exercisesName, SqlExYamlFormat(baseValues).read)
    } yield {
      for (exFailure <- exercises._2)
      // FIXME: return...
        logger.error("Could not read sql exercise: ", exFailure.exception)

      SqlCompleteScenario(SqlScenario(baseValues.id, baseValues.semanticVersion, baseValues.title, baseValues.author, baseValues.text, baseValues.state, shortName), exercises._1)
    }

    override def write(completeEx: SqlCompleteScenario): YamlObject = YamlObject(
      writeBaseValues(completeEx.coll) ++
        Map(
          YamlString(shortNameName) -> YamlString(completeEx.coll.shortName),
          YamlString(exercisesName) -> YamlArr(completeEx.exercises map SqlExYamlFormat(completeEx.coll.baseValues).write)
        )
    )
  }

  final case class SqlExYamlFormat(collBaseValues: BaseValues) extends MyYamlObjectFormat[SqlCompleteEx] {

    override protected def readObject(yamlObject: YamlObject): Try[SqlCompleteEx] = for {
      baseValues <- readBaseValues(yamlObject)

      exerciseType <- yamlObject.enumField(exerciseTypeName, SqlExerciseType.withNameInsensitiveOption) map (_ getOrElse SqlExerciseType.SELECT)
      tagTries <- yamlObject.optArrayField(tagsName, _.asStringEnum(SqlExTag.withNameInsensitiveOption(_) getOrElse SqlExTag.SQL_JOIN))

      hint <- yamlObject.optStringField(hintName)

      sampleTries <- yamlObject.arrayField(samplesName, SqlSampleYamlFormat(collBaseValues, baseValues).read)
    } yield {

      for (tagFailures <- tagTries._2)
      // FIXME: return...
        logger.error("Could not read sql tag", tagFailures.exception)

      for (sampleFailure <- sampleTries._2)
      // FIXME: return...
        logger.error("Could not read sql sample", sampleFailure.exception)

      SqlCompleteEx(SqlExercise(baseValues.id, baseValues.semanticVersion, baseValues.title, baseValues.author, baseValues.text, baseValues.state,
        collBaseValues.id, collBaseValues.semanticVersion, exerciseType, tagTries._1.mkString(tagJoinChar), hint), sampleTries._1)
    }

    override def write(completeEx: SqlCompleteEx): YamlValue = YamlObject(
      writeBaseValues(completeEx.ex) ++
        Map[YamlValue, YamlValue](
          YamlString(exerciseTypeName) -> YamlString(completeEx.ex.exerciseType.entryName),
          YamlString(samplesName) -> YamlArr(completeEx.samples map SqlSampleYamlFormat(collBaseValues, completeEx.ex.baseValues).write)
        ) ++ completeEx.ex.hint.map(h => YamlString(hintName) -> YamlString(h)) ++ writeTags(completeEx)
    )

    private def writeTags(completeEx: SqlCompleteEx): Option[(YamlValue, YamlValue)] = completeEx.tags match {
      case Nil  => None
      case tags => Some(YamlString(tagsName) -> YamlArr(tags.map(t => YamlString(t.entryName))))
    }

  }


  final case class SqlSampleYamlFormat(collBaseValues: BaseValues, exerciseBaseValues: BaseValues) extends MyYamlObjectFormat[SqlSample] {

    override def readObject(yamlObject: YamlObject): Try[SqlSample] = for {
      id <- yamlObject.intField(idName)
      sample <- yamlObject.stringField(sampleName)
    } yield SqlSample(id, exerciseBaseValues.id, exerciseBaseValues.semanticVersion, collBaseValues.id, collBaseValues.semanticVersion, sample)

    override def write(obj: SqlSample): YamlValue = YamlObj(
      idName -> obj.id,
      sampleName -> obj.sample
    )

  }

}
