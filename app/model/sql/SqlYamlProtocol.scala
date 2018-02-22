package model.sql

import model.MyYamlProtocol._
import model.sql.SqlConsts._
import model.sql.SqlEnums.{SqlExTag, SqlExerciseType}
import model.{BaseValues, MyYamlProtocol, YamlArr, YamlObj}
import net.jcazevedo.moultingyaml._
import play.api.Logger

import scala.language.postfixOps
import scala.util.Try

object SqlYamlProtocol extends MyYamlProtocol {

  implicit object SqlScenarioYamlFormat extends HasBaseValuesYamlFormat[SqlCompleteScenario] {

    override protected def readRest(yamlObject: YamlObject, baseValues: BaseValues): Try[SqlCompleteScenario] = for {
      shortName <- yamlObject.stringField(SHORTNAME_NAME)
      exercises <- yamlObject.arrayField(EXERCISES_NAME, SqlExYamlFormat(baseValues.id).read)
    } yield {
      for (exFailure <- exercises._2)
      // FIXME: return...
        Logger.error("Could not read sql exercise: ", exFailure.exception)

      SqlCompleteScenario(SqlScenario(baseValues, shortName), exercises._1)
    }

    override protected def writeRest(completeEx: SqlCompleteScenario): Map[YamlValue, YamlValue] = Map(
      YamlString(SHORTNAME_NAME) -> YamlString(completeEx.coll.shortName),
      YamlString(EXERCISES_NAME) -> YamlArr(completeEx.exercises map SqlExYamlFormat(completeEx.id).write)
    )
  }


  case class SqlExYamlFormat(scenarioId: Int) extends HasBaseValuesYamlFormat[SqlCompleteEx] {

    override protected def readRest(yamlObject: YamlObject, baseValues: BaseValues): Try[SqlCompleteEx] = for {
      exerciseType <- yamlObject.enumField(ExerciseTypeName, SqlExerciseType.valueOf)
      tagTries <- yamlObject.optArrayField(TAGS_NAME, _.asStringEnum(SqlExTag.byString(_).getOrElse(SqlExTag.SQL_JOIN)))
      hint <- yamlObject.optStringField(HINT_NAME)
      sampleTries <- yamlObject.arrayField("samples", SqlSampleYamlFormat(scenarioId, baseValues.id).read)
    } yield {
      for (tagFailures <- tagTries._2)
      // FIXME: return...
        Logger.error("Could not read sql tag", tagFailures.exception)

      for (sampleFailure <- sampleTries._2)
      // FIXME: return...
        Logger.error("Could not read sql sample", sampleFailure.exception)

      SqlCompleteEx(new SqlExercise(baseValues, scenarioId, exerciseType, tagTries._1.mkString(TagJoinChar), hint), sampleTries._1)
    }

    override protected def writeRest(completeEx: SqlCompleteEx): Map[YamlValue, YamlValue] = Map(
      YamlString(ExerciseTypeName) -> YamlString(completeEx.ex.exerciseType.name),
      YamlString("samples") -> YamlArr(completeEx.samples map SqlSampleYamlFormat(completeEx.ex.collectionId, completeEx.id).write)
    ) ++ completeEx.ex.hint.map(h => YamlString(HINT_NAME) -> YamlString(h)) ++ writeTags(completeEx)

    private def writeTags(completeEx: SqlCompleteEx): Option[(YamlValue, YamlValue)] = completeEx.tags match {
      case Nil  => None
      case tags => Some(YamlString(TAGS_NAME) -> YamlArr(tags map (t => YamlString(t.name))))
    }

  }


  case class SqlSampleYamlFormat(scenarioId: Int, exerciseId: Int) extends MyYamlObjectFormat[SqlSample] {

    override def readObject(yamlObject: YamlObject): Try[SqlSample] = for {
      id <- yamlObject.intField(ID_NAME)
      sample <- yamlObject.stringField("sample")
    } yield SqlSample(id, exerciseId, scenarioId, sample)

    override def write(obj: SqlSample): YamlValue = YamlObj(
      ID_NAME -> obj.id,
      "sample" -> obj.sample
    )

  }

}
