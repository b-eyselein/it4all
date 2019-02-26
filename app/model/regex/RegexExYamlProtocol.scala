package model.regex

import model.MyYamlProtocol._
import model.core.CoreConsts.{sampleName => _, samplesName => _, semanticVersionName => _, _}
import model.regex.RegexConsts._
import model.sql.SqlConsts.{authorName, idName, shortNameName, statusName, textName, titleName}
import model.sql.SqlScenario
import model.{ExerciseState, MyYamlProtocol, SemanticVersion, SemanticVersionHelper}
import net.jcazevedo.moultingyaml.{YamlObject, YamlValue}

import scala.util.Try

object RegexExYamlProtocol extends MyYamlProtocol {

  object RegexCollectionYamlFormat extends MyYamlObjectFormat[RegexCollection] {

    override protected def readObject(yamlObject: YamlObject): Try[RegexCollection] = for {
      id <- yamlObject.intField(idName)
      title <- yamlObject.stringField(titleName)
      author <- yamlObject.stringField(authorName)
      text <- yamlObject.stringField(textName)
      state <- yamlObject.enumField(statusName, ExerciseState.withNameInsensitiveOption) map (_ getOrElse ExerciseState.CREATED)
      shortName <- yamlObject.stringField(shortNameName)
    } yield RegexCollection(id, title, author, text, state, shortName)

    override def write(obj: RegexCollection): YamlValue = ???

  }

  final case class RegexExYamlFormat(collId: Int) extends MyYamlObjectFormat[RegexExercise] {

    override protected def readObject(yamlObject: YamlObject): Try[RegexExercise] = for {
      id <- yamlObject.intField(idName)
      title <- yamlObject.stringField(titleName)
      author <- yamlObject.stringField(authorName)
      text <- yamlObject.stringField(textName)
      state: ExerciseState <- yamlObject.enumField(statusName, ExerciseState.withNameInsensitiveOption) map (_ getOrElse ExerciseState.CREATED)
      semanticVersion <- yamlObject.someField(semanticVersionName) flatMap SemanticVersionHelper.semanticVersionYamlField

      sampleSolutionTries <- yamlObject.arrayField(samplesName, RegexSampleSolutionYamlFormat(id, semanticVersion, collId).read)

      testDataTries <- yamlObject.arrayField(testDataName, RegexTestDataYamlFormat(id, semanticVersion, collId).read)
    } yield {

      for (sampleSolReadError <- sampleSolutionTries._2)
        println(sampleSolReadError)

      for (testDataReadError <- testDataTries._2)
        println(testDataReadError)

      RegexExercise(id, semanticVersion, collId, title, author, text, state, sampleSolutionTries._1, testDataTries._1)
    }

    override def write(obj: RegexExercise): YamlValue = ???

  }

  private final case class RegexSampleSolutionYamlFormat(exId: Int, exSemVer: SemanticVersion, collId: Int) extends MyYamlObjectFormat[RegexSampleSolution] {

    override protected def readObject(yamlObject: YamlObject): Try[RegexSampleSolution] = for {
      id <- yamlObject.intField(idName)
      sample <- yamlObject.stringField(sampleName)
    } yield RegexSampleSolution(id, exId, exSemVer, collId, sample)

    override def write(obj: RegexSampleSolution): YamlValue = ???

  }

  private final case class RegexTestDataYamlFormat(exId: Int, exSemVer: SemanticVersion, collId: Int) extends MyYamlObjectFormat[RegexTestData] {

    override protected def readObject(yamlObject: YamlObject): Try[RegexTestData] = for {
      id <- yamlObject.intField(idName)
      data <- yamlObject.stringField(dataName)
      isIncluded <- yamlObject.boolField(includedName)
    } yield RegexTestData(id, exId, exSemVer, collId, data, isIncluded)

    override def write(obj: RegexTestData): YamlValue = ???

  }

}
