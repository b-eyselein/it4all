package model.tools.sql

import model.MyYamlProtocol._
import model.tools.sql.SqlConsts._
import model.{MyYamlProtocol, StringSampleSolution, YamlObj}
import net.jcazevedo.moultingyaml.{YamlObject, YamlValue}
import play.api.Logger

import scala.util.Try

object SqlYamlProtocol extends MyYamlProtocol {

  private val logger = Logger(SqlYamlProtocol.getClass)

  object SqlExerciseYamlFormat extends MyYamlObjectFormat[SqlExercise] {

    override def write(obj: SqlExercise): YamlValue = ???

    override protected def readObject(yamlObject: YamlObject): Try[SqlExercise] = for {
      baseValues <- readBaseValues(yamlObject)

      exerciseType <- yamlObject.enumField(exerciseTypeName, SqlExerciseType.withNameInsensitiveOption).map(_ getOrElse SqlExerciseType.SELECT)
      tagTries <- yamlObject.optArrayField(tagsName, _.asStringEnum(SqlExerciseTag.withNameInsensitive))

      hint <- yamlObject.optStringField(hintName)

      sampleTries <- yamlObject.arrayField(samplesName, SqlSampleYamlFormat.read)
    } yield {

      for (tagFailures <- tagTries._2)
      // FIXME: return...
        logger.error("Could not read sql tag", tagFailures.exception)

      //      for (sampleFailure <- sampleTries._2)
      // // FIXME: return...
      //        logger.error("Could not read sql sample", sampleFailure.exception)

      SqlExercise(baseValues.id, baseValues.collId, baseValues.semanticVersion, baseValues.title, baseValues.author, baseValues.text, baseValues.state,
        exerciseType, tagTries._1, hint, sampleTries._1)
    }
  }


  private object SqlSampleYamlFormat extends MyYamlObjectFormat[StringSampleSolution] {

    override def readObject(yamlObject: YamlObject): Try[StringSampleSolution] = for {
      id <- yamlObject.intField(idName)
      sample <- yamlObject.stringField(sampleName)
    } yield StringSampleSolution(id, sample)

    override def write(obj: StringSampleSolution): YamlValue = YamlObj(
      idName -> obj.id,
      sampleName -> obj.sample
    )

  }

}
