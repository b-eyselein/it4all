package model.sql

import model.{ExerciseState, MyYamlProtocol, SemanticVersion, SemanticVersionHelper}
import model.MyYamlProtocol._
import model.core.CoreConsts.{authorName, idName, semanticVersionName, statusName, textName, titleName}
import model.sql.SqlConsts._
import model.sql.SqlYamlProtocol.{SqlSampleYamlFormat, logger, readBaseValues}
import net.jcazevedo.moultingyaml.{YamlObject, YamlValue}
import play.api.Logger

import scala.util.Try

object NewSqlYamlProtocol extends MyYamlProtocol {

  private val logger = Logger(NewSqlYamlProtocol.getClass)

  object SqlCollectionYamlFormat extends MyYamlObjectFormat[SqlScenario] {

    override protected def readObject(yamlObject: YamlObject): Try[SqlScenario] = for {
      id <- yamlObject.intField(idName)
      title <- yamlObject.stringField(titleName)
      author <- yamlObject.stringField(authorName)
      text <- yamlObject.stringField(textName)
      state <- yamlObject.enumField(statusName, ExerciseState.withNameInsensitiveOption) map (_ getOrElse ExerciseState.CREATED)
      shortName <- yamlObject.stringField(shortNameName)
    } yield SqlScenario(id, title, author, text, state, shortName)

    override def write(obj: SqlScenario): YamlValue = ???

  }

  final case class SqlExerciseYamlFormat(collId: Int) extends MyYamlObjectFormat[SqlExercise] {

    //    override protected def readObject(yamlObject: YamlObject): Try[SqlExercise] = ???

    override def write(obj: SqlExercise): YamlValue = ???


    override protected def readObject(yamlObject: YamlObject): Try[SqlExercise] = for {
      baseValues <- readBaseValues(yamlObject)

      exerciseType <- yamlObject.enumField(exerciseTypeName, SqlExerciseType.withNameInsensitiveOption) map (_ getOrElse SqlExerciseType.SELECT)
      tagTries <- yamlObject.optArrayField(tagsName, _.asStringEnum(SqlExTag.withNameInsensitiveOption(_) getOrElse SqlExTag.SQL_JOIN))

      hint <- yamlObject.optStringField(hintName)

      sampleTries <- yamlObject.arrayField(samplesName, SqlSampleYamlFormat(collId, baseValues).read)
    } yield {

      for (tagFailures <- tagTries._2)
      // FIXME: return...
        logger.error("Could not read sql tag", tagFailures.exception)

      //      for (sampleFailure <- sampleTries._2)
      // // FIXME: return...
      //        logger.error("Could not read sql sample", sampleFailure.exception)

      SqlExercise(baseValues.id, baseValues.semanticVersion, baseValues.title, baseValues.author, baseValues.text, baseValues.state,
        collId, exerciseType, tagTries._1, hint, sampleTries._1)
    }
  }

}
