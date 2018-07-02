package model.sql

import model.MyYamlProtocol._
import model._
import model.sql.SqlConsts._
import net.jcazevedo.moultingyaml._
import play.api.Logger

import scala.language.postfixOps
import scala.util.Try

object SqlYamlProtocol extends MyYamlProtocol {

  implicit object SqlScenarioYamlFormat extends MyYamlObjectFormat[SqlCompleteScenario] {

    override protected def readObject(yamlObject: YamlObject): Try[SqlCompleteScenario] = for {
      baseValues <- readBaseValues(yamlObject)

      shortName <- yamlObject.stringField(shortNameName)
      exercises <- yamlObject.arrayField(exercisesName, SqlExYamlFormat(baseValues._1).read)
    } yield {
      for (exFailure <- exercises._2)
      // FIXME: return...
        Logger.error("Could not read sql exercise: ", exFailure.exception)

      SqlCompleteScenario(SqlScenario(baseValues._1, baseValues._2, baseValues._3, baseValues._4, baseValues._5, baseValues._6, shortName), exercises._1)
    }

    override def write(completeEx: SqlCompleteScenario): YamlObject = YamlObject(
      writeBaseValues(completeEx.coll) ++
        Map(
          YamlString(shortNameName) -> YamlString(completeEx.coll.shortName),
          YamlString(exercisesName) -> YamlArr(completeEx.exercises map SqlExYamlFormat(completeEx.coll.id).write)
        )
    )
  }

  case class SqlExYamlFormat(scenarioId: Int) extends MyYamlObjectFormat[SqlCompleteEx] {

    override protected def readObject(yamlObject: YamlObject): Try[SqlCompleteEx] = for {
      baseValues <- readBaseValues(yamlObject)

      exerciseType <- yamlObject.enumField(exerciseTypeName, SqlExerciseType.withNameInsensitiveOption) map (_ getOrElse SqlExerciseType.SELECT)
      tagTries <- yamlObject.optArrayField(tagsName, _.asStringEnum(SqlExTag.withNameInsensitiveOption(_) getOrElse SqlExTag.SQL_JOIN))

      hint <- yamlObject.optStringField(hintName)

      sampleTries <- yamlObject.arrayField(samplesName, SqlSampleYamlFormat(scenarioId, baseValues._1).read)
    } yield {

      for (tagFailures <- tagTries._2)
      // FIXME: return...
        Logger.error("Could not read sql tag", tagFailures.exception)

      for (sampleFailure <- sampleTries._2)
      // FIXME: return...
        Logger.error("Could not read sql sample", sampleFailure.exception)

      SqlCompleteEx(SqlExercise(baseValues._1, baseValues._2, baseValues._3, baseValues._4, baseValues._5, baseValues._6,
        scenarioId, exerciseType, tagTries._1 mkString tagJoinChar, hint), sampleTries._1)
    }

    override def write(completeEx: SqlCompleteEx): YamlValue = YamlObject(
      writeBaseValues(completeEx.ex) ++
        Map(
          YamlString(exerciseTypeName) -> YamlString(completeEx.ex.exerciseType.entryName),
          YamlString(samplesName) -> YamlArr(completeEx.samples map SqlSampleYamlFormat(completeEx.ex.collectionId, completeEx.ex.id).write)
        ) ++ completeEx.ex.hint.map(h => YamlString(hintName) -> YamlString(h)) ++ writeTags(completeEx)
    )

    private def writeTags(completeEx: SqlCompleteEx): Option[(YamlValue, YamlValue)] = completeEx.tags match {
      case Nil  => None
      case tags => Some(YamlString(tagsName) -> YamlArr(tags map (t => YamlString(t.entryName))))
    }

  }


  case class SqlSampleYamlFormat(scenarioId: Int, exerciseId: Int) extends MyYamlObjectFormat[SqlSample] {

    override def readObject(yamlObject: YamlObject): Try[SqlSample] = for {
      id <- yamlObject.intField(idName)
      sample <- yamlObject.stringField(sampleName)
    } yield SqlSample(id, exerciseId, scenarioId, sample)

    override def write(obj: SqlSample): YamlValue = YamlObj(
      idName -> obj.id,
      sampleName -> obj.sample
    )

  }

}
