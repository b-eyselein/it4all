package model.tools.regex

import model.MyYamlProtocol._
import model.tools.regex.RegexConsts._
import model.{ExerciseState, MyYamlProtocol, SemanticVersionHelper}
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

  object RegexExYamlFormat extends MyYamlObjectFormat[RegexExercise] {

    override protected def readObject(yamlObject: YamlObject): Try[RegexExercise] = for {
      id <- yamlObject.intField(idName)
      title <- yamlObject.stringField(titleName)
      author <- yamlObject.stringField(authorName)
      text <- yamlObject.stringField(textName)
      state: ExerciseState <- yamlObject.enumField(statusName, ExerciseState.withNameInsensitiveOption) map (_ getOrElse ExerciseState.CREATED)
      semanticVersion <- yamlObject.someField(semanticVersionName) flatMap SemanticVersionHelper.semanticVersionYamlField

      sampleSolutionTries <- yamlObject.arrayField(samplesName, RegexSampleSolutionYamlFormat.read)

      testDataTries <- yamlObject.arrayField(testDataName, RegexTestDataYamlFormat.read)
    } yield {

      for (sampleSolReadError <- sampleSolutionTries._2)
        println(sampleSolReadError)

      for (testDataReadError <- testDataTries._2)
        println(testDataReadError)

      RegexExercise(id, semanticVersion, title, author, text, state, sampleSolutionTries._1, testDataTries._1)
    }

    override def write(obj: RegexExercise): YamlValue = ???

  }

  private object RegexSampleSolutionYamlFormat extends MyYamlObjectFormat[RegexSampleSolution] {

    override protected def readObject(yamlObject: YamlObject): Try[RegexSampleSolution] = for {
      id <- yamlObject.intField(idName)
      sample <- yamlObject.stringField(sampleName)
    } yield RegexSampleSolution(id, sample)

    override def write(obj: RegexSampleSolution): YamlValue = ???

  }

  private object RegexTestDataYamlFormat extends MyYamlObjectFormat[RegexTestData] {

    override protected def readObject(yamlObject: YamlObject): Try[RegexTestData] = for {
      id <- yamlObject.intField(idName)
      data <- yamlObject.stringField(dataName)
      isIncluded <- yamlObject.boolField(includedName)
    } yield RegexTestData(id, data, isIncluded)

    override def write(obj: RegexTestData): YamlValue = ???

  }

}
