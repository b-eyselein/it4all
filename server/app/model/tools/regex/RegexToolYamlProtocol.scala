package model.tools.regex

import model.MyYamlProtocol._
import model.tools.regex.RegexConsts._
import model.{ExerciseState, MyYamlProtocol, SemanticVersionHelper}
import net.jcazevedo.moultingyaml.{YamlObject, YamlValue}

import scala.util.Try

object RegexToolYamlProtocol extends MyYamlProtocol {

  object RegexExYamlFormat extends MyYamlObjectFormat[RegexExercise] {

    override protected def readObject(yamlObject: YamlObject): Try[RegexExercise] = for {
      id <- yamlObject.intField(idName)
      collectionId <- yamlObject.intField(collectionIdName)
      semanticVersion <- yamlObject.someField(semanticVersionName) flatMap SemanticVersionHelper.semanticVersionYamlField
      title <- yamlObject.stringField(titleName)
      author <- yamlObject.stringField(authorName)
      text <- yamlObject.stringField(textName)
      state: ExerciseState <- yamlObject.enumField(statusName, ExerciseState.withNameInsensitiveOption).map(_ getOrElse ExerciseState.CREATED)
      maxPoints <- yamlObject.intField(maxPointsName)

      correctionType <- yamlObject.enumField(correctionTypeName, RegexCorrectionTypes.withNameInsensitive)
      sampleSolutionTries <- yamlObject.arrayField(samplesName, StringSampleSolutionYamlFormat.read)

      matchTestDataTries <- yamlObject.arrayField(matchTestDataName, RegexMatchTestDataYamlFormat.read)
      extractionTestDataTries <- yamlObject.arrayField(extractionTestDataName, RegexExtractionTestDataYamlFormat.read)
    } yield {

      for (sampleSolReadError <- sampleSolutionTries._2)
        println(sampleSolReadError)

      for (testDataReadError <- matchTestDataTries._2)
        println(testDataReadError)

      for (extractionTestDataError <- extractionTestDataTries._2)
        println(extractionTestDataError)

      RegexExercise(
        id,collectionId, semanticVersion, title, author, text, state, maxPoints, correctionType,
        sampleSolutionTries._1, matchTestDataTries._1, extractionTestDataTries._1
      )
    }

    override def write(obj: RegexExercise): YamlValue = ???

  }

  private object RegexMatchTestDataYamlFormat extends MyYamlObjectFormat[RegexMatchTestData] {

    override protected def readObject(yamlObject: YamlObject): Try[RegexMatchTestData] = for {
      id <- yamlObject.intField(idName)
      data <- yamlObject.stringField(dataName)
      isIncluded <- yamlObject.boolField(includedName)
    } yield RegexMatchTestData(id, data, isIncluded)

    override def write(obj: RegexMatchTestData): YamlValue = ???

  }

  private object RegexExtractionTestDataYamlFormat extends MyYamlObjectFormat[RegexExtractionTestData] {

    override protected def readObject(yamlObject: YamlObject): Try[RegexExtractionTestData] = for {
      id <- yamlObject.intField(idName)
      base <- yamlObject.stringField("base")
    } yield RegexExtractionTestData(id, base)

    override def write(obj: RegexExtractionTestData): YamlValue = ???

  }

}
