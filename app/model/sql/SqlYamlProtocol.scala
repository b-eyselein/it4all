package model.sql

import model.{BaseValues, MyYamlProtocol}
import model.MyYamlProtocol._
import model.sql.SqlConsts._
import model.sql.SqlEnums.SqlExerciseType
import net.jcazevedo.moultingyaml.{YamlObject, YamlValue}

object SqlYamlProtocol extends MyYamlProtocol {

  implicit object SqlScenarioYamlFormat extends HasBaseValuesYamlFormat[SqlCompleteScenario] {

    override protected def readRest(yamlObject: YamlObject, baseValues: BaseValues): SqlCompleteScenario = SqlCompleteScenario(
      SqlScenario(baseValues, yamlObject.stringField(SHORTNAME_NAME), yamlObject.stringField(SCRIPTFILE_NAME)),
      yamlObject.arrayField(EXERCISES_NAME, _ convertTo[SqlCompleteEx] SqlExYamlFormat(baseValues.id))
    )

    override protected def writeRest(completeEx: SqlCompleteScenario): Map[YamlValue, YamlValue] = ???
  }

  case class SqlExYamlFormat(scenarioId: Int) extends HasBaseValuesYamlFormat[SqlCompleteEx] {

    override protected def readRest(yamlObject: YamlObject, baseValues: BaseValues): SqlCompleteEx =
      SqlCompleteEx(
        new SqlExercise(baseValues, scenarioId,
          yamlObject.enumField(ExerciseTypeName, SqlExerciseType.valueOf, SqlExerciseType.SELECT),
          yamlObject.optStringField(HINT_NAME)),
        tags = Seq.empty,
        samples = Seq.empty)

    override protected def writeRest(completeEx: SqlCompleteEx): Map[YamlValue, YamlValue] = ???
  }

}
