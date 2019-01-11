package model.regex

import model.MyYamlProtocol._
import model.regex.RegexConsts._
import model.{MyYamlProtocol, SemanticVersion}
import net.jcazevedo.moultingyaml.{YamlObject, YamlValue}

import scala.util.Try

object RegexExYamlProtocol extends MyYamlProtocol {

  implicit object RegexExYamlFormat extends MyYamlObjectFormat[RegexCompleteEx] {

    override protected def readObject(yamlObject: YamlObject): Try[RegexCompleteEx] = for {
      baseValues <- readBaseValues(yamlObject)

      sampleSolutionTries <- yamlObject.arrayField(samplesName, RegexSampleSolutionYamlFormat(baseValues.id, baseValues.semanticVersion).read)

      testDataTries <- yamlObject.arrayField(testDataName, RegexTestDataYamlFormat(baseValues.id, baseValues.semanticVersion).read)
    } yield {

      for (sampleSolReadError <- sampleSolutionTries._2)
        println(sampleSolReadError)

      for (testDataReadError <- testDataTries._2)
        println(testDataReadError)

      RegexCompleteEx(
        RegexExercise(baseValues.id, baseValues.title, baseValues.author, baseValues.text, baseValues.state, baseValues.semanticVersion),
        sampleSolutionTries._1, testDataTries._1
      )
    }

    override def write(obj: RegexCompleteEx): YamlValue = ???

  }

  private final case class RegexSampleSolutionYamlFormat(exId: Int, exSemVer: SemanticVersion) extends MyYamlObjectFormat[RegexSampleSolution] {

    override protected def readObject(yamlObject: YamlObject): Try[RegexSampleSolution] = for {
      id <- yamlObject.intField(idName)
      sample <- yamlObject.stringField(sampleName)
    } yield RegexSampleSolution(id, exId, exSemVer, sample)

    override def write(obj: RegexSampleSolution): YamlValue = ???

  }

  private final case class RegexTestDataYamlFormat(exId: Int, exSemVer: SemanticVersion) extends MyYamlObjectFormat[RegexTestData] {

    override protected def readObject(yamlObject: YamlObject): Try[RegexTestData] = for {
      id <- yamlObject.intField(idName)
      data <- yamlObject.stringField(dataName)
      isIncluded <- yamlObject.boolField(includedName)
    } yield RegexTestData(id, exId, exSemVer, data, isIncluded)

    override def write(obj: RegexTestData): YamlValue = ???

  }

}
