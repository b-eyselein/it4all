package model.sql

import model.MyYamlProtocol._
import model.sql.SqlConsts._
import model.sql.SqlEnums.{SqlExTag, SqlExerciseType}
import model.{BaseValues, MyYamlProtocol}
import net.jcazevedo.moultingyaml._

import scala.language.postfixOps

object SqlYamlProtocol extends MyYamlProtocol {

  implicit object SqlScenarioYamlFormat extends HasBaseValuesYamlFormat[SqlCompleteScenario] {

    override protected def readRest(yamlObject: YamlObject, baseValues: BaseValues): SqlCompleteScenario = SqlCompleteScenario(
      SqlScenario(baseValues, yamlObject.stringField(SHORTNAME_NAME)),
      yamlObject.arrayField(EXERCISES_NAME, _ convertTo[SqlCompleteEx] SqlExYamlFormat(baseValues.id))
    )

    override protected def writeRest(completeEx: SqlCompleteScenario): Map[YamlValue, YamlValue] = Map(
      YamlString(SHORTNAME_NAME) -> YamlString(completeEx.coll.shortName),
      YamlString(EXERCISES_NAME) -> YamlArray(completeEx.exercises map (_ toYaml SqlExYamlFormat(completeEx.id)) toVector)
    )
  }


  case class SqlExYamlFormat(scenarioId: Int) extends HasBaseValuesYamlFormat[SqlCompleteEx] {

    override protected def readRest(yamlObject: YamlObject, baseValues: BaseValues): SqlCompleteEx =
      SqlCompleteEx(
        new SqlExercise(baseValues, scenarioId,
          yamlObject.enumField(ExerciseTypeName, SqlExerciseType.valueOf, SqlExerciseType.SELECT),
          yamlObject.optArrayField(TAGS_NAME, _.asStr flatMap SqlExTag.byString).flatten mkString TagJoinChar,
          yamlObject.optStringField(HINT_NAME)),
        yamlObject.arrayField("samples", _ convertTo[SqlSample] SqlSampleYamlFormat(scenarioId, baseValues.id))
      )

    override protected def writeRest(completeEx: SqlCompleteEx): Map[YamlValue, YamlValue] = Map(
      YamlString(ExerciseTypeName) -> YamlString(completeEx.ex.exerciseType.name),
      YamlString("samples") -> YamlArray(completeEx.samples map (_ toYaml SqlSampleYamlFormat(completeEx.ex.collectionId, completeEx.id)) toVector)
    ) ++ completeEx.ex.hint.map(h => YamlString(HINT_NAME) -> YamlString(h)) ++ writeTags(completeEx)

    private def writeTags(completeEx: SqlCompleteEx): Option[(YamlValue, YamlValue)] = completeEx.tags match {
      case Nil  => None
      case tags => Some(YamlString(TAGS_NAME) -> YamlArray(tags map (t => YamlString(t.name)) toVector))
    }

  }


  case class SqlSampleYamlFormat(scenarioId: Int, exerciseId: Int) extends MyYamlFormat[SqlSample] {

    override def readObject(yamlObject: YamlObject): SqlSample =
      SqlSample(yamlObject.intField(ID_NAME), exerciseId, scenarioId, yamlObject.stringField("sample"))

    override def write(obj: SqlSample): YamlValue = YamlObject(
      YamlString(ID_NAME) -> obj.id,
      YamlString("sample") -> obj.sample
    )

  }

}
