package model.tools.programming

import enumeratum.{EnumEntry, PlayEnum}
import model._
import model.points.Points
import model.tools.uml.UmlClassDiagram
import play.api.libs.json.JsValue
import play.twirl.api.Html

import scala.collection.immutable


final case class ProgCollection(id: Int, title: String, author: String, text: String, state: ExerciseState, shortName: String)
  extends ExerciseCollection

final case class ProgExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                              functionName: String, outputType: ProgDataType, baseData: Option[JsValue],
                              unitTestType: UnitTestType,
                              inputTypes: Seq[ProgInput],
                              sampleSolutions: Seq[ProgSampleSolution],
                              sampleTestData: Seq[ProgSampleTestData],
                              unitTestsDescription: String,
                              unitTestTestConfigs: Seq[UnitTestTestConfig],
                              maybeClassDiagramPart: Option[UmlClassDiagram]
                             ) extends Exercise with FileExercise[ProgExPart] {

  override def baseValues: BaseValues = BaseValues(id, semanticVersion, title, author, text, state)

  // Other methods

  val inputCount: Int = inputTypes.size

  val unitTestFileName = "test.py" // TODO: s"${functionName}_test.py" ?

  def buildTestDataFileContent(completeTestData: Seq[ProgTestData], extendedUnitTests: Boolean = false): JsValue = {
    // FIXME: update...

    if (extendedUnitTests) ???
    else TestDataJsonFormat.dumpTestDataToJson(this, completeTestData)
  }

  def solutionFileName: String = s"${functionName}.py"

  override def filesForExercisePart(part: ProgExPart): Seq[ExerciseFile] = part match {
    case ProgExParts.TestCreation =>
      Seq(
        ExerciseFile(unitTestFileName, buildUnitTestFile, "python", true),
        ExerciseFile(solutionFileName, buildSolutionFile, "python", false)
      )
    case _                        => Seq.empty
  }

  def buildUnitTestFile: String =
    s"""import unittest
       |from ${functionName} import ${functionName}
       |
       |class ${functionName.capitalize}Test(unittest.TestCase):
       |    def test_${functionName}(self):
       |        pass""".stripMargin

  def buildSolutionFile: String = {
    val x = inputTypes.map(it => it.inputName + ": " + it.inputType.typeName).mkString(",")

    s"""def ${functionName}(${x}) -> ${outputType.typeName}:
       |    # implementation hidden...
       |    pass""".stripMargin
  }

  override def preview: Html = // FIXME: move to toolMain!
    views.html.toolViews.programming.progPreview(this)

}


sealed trait UnitTestType extends EnumEntry

case object UnitTestTypes extends PlayEnum[UnitTestType] {

  override val values: immutable.IndexedSeq[UnitTestType] = findValues


  case object Simplified extends UnitTestType

  case object Normal extends UnitTestType

}


final case class ProgInput(id: Int, inputName: String, inputType: ProgDataType)

final case class ProgSolution(implementation: String, testData: Seq[ProgUserTestData], unitTest: String = "") {

  def language: ProgLanguage = ProgLanguages.PYTHON_3

}


sealed trait ProgTestData {

  val id         : Int
  val inputAsJson: JsValue
  val output     : JsValue

}

final case class ProgSampleTestData(id: Int, inputAsJson: JsValue, output: JsValue) extends ProgTestData

final case class ProgUserTestData(id: Int, inputAsJson: JsValue, output: JsValue, state: ExerciseState) extends ProgTestData

// Solution types

final case class ProgSampleSolution(id: Int, base: String, solutionStr: String)
  extends SampleSolution[ProgSolution] {

  def language: ProgLanguage = ProgLanguages.PYTHON_3

  val part: ProgExPart = ProgExParts.Implementation

  val sample: ProgSolution = part match {
    case ProgExParts.TestCreation => ??? // ProgSolution(solutionStr = "", language)
    case _                        => ProgSolution(implementation = solutionStr, testData = Seq[ProgUserTestData]())
  }

}

final case class ProgUserSolution(id: Int, part: ProgExPart, solution: ProgSolution, points: Points, maxPoints: Points)
  extends UserSolution[ProgExPart, ProgSolution] {

  def language: ProgLanguage = ProgLanguages.PYTHON_3

  def commitedTestData: Seq[ProgUserTestData] = solution.testData

}

final case class ProgExerciseReview(difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview
