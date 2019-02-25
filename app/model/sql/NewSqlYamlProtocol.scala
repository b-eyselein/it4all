package model.sql

import model.MyYamlProtocol
import model.MyYamlProtocol._
import model.sql.SqlConsts.shortNameName
import net.jcazevedo.moultingyaml.{YamlObject, YamlValue}

import scala.util.Try

object NewSqlYamlProtocol extends MyYamlProtocol {

  object SqlCollectionYamlFormat extends MyYamlObjectFormat[SqlScenario] {

    override protected def readObject(yamlObject: YamlObject): Try[SqlScenario] = for {
      baseValues <- readBaseValues(yamlObject)

      shortName <- yamlObject.stringField(shortNameName)
    } yield SqlScenario(baseValues.id, baseValues.semanticVersion, baseValues.title, baseValues.author, baseValues.text, baseValues.state, shortName)

    override def write(obj: SqlScenario): YamlValue = ???

  }

  object SqlExerciseYamlFormat extends MyYamlObjectFormat[SqlExercise] {

    override protected def readObject(yamlObject: YamlObject): Try[SqlExercise] = ???

    override def write(obj: SqlExercise): YamlValue = ???

  }

}
