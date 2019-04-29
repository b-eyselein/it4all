package model.tools.programming

import model._
import model.points.Points
import model.tools.uml.UmlClassDiagram
import play.api.libs.json.JsValue
import play.twirl.api.Html

final case class ProgCollection(id: Int, title: String, author: String, text: String, state: ExerciseState, shortName: String)
  extends ExerciseCollection

final case class ProgExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                              folderIdentifier: String, functionName: String, outputType: ProgDataType, baseData: Option[JsValue],
                              inputTypes: Seq[ProgInput],
                              sampleSolutions: Seq[ProgSampleSolution],
                              sampleTestData: Seq[ProgSampleTestData],
                              maybeClassDiagramPart: Option[UmlClassDiagram]
                             ) extends Exercise {

  override def baseValues: BaseValues = BaseValues(id, semanticVersion, title, author, text, state)

  // Other methods

  val inputCount: Int = inputTypes.size

  override def preview: Html = // FIXME: move to toolMain!
    views.html.toolViews.programming.progPreview(this)

  def buildTestDataFileContent(completeTestData: Seq[ProgTestData], extendedUnitTests: Boolean = false): JsValue = {
    // FIXME: update...

    if (extendedUnitTests) ???
    else TestDataJsonFormat.dumpTestDataToJson(this, completeTestData)
  }

}


final case class ProgInput(id: Int, inputName: String, inputType: ProgDataType)

final case class ProgSolution(implementation: String, testData: Seq[ProgUserTestData]) {

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
    case ProgExParts.TestdataCreation => ??? // ProgSolution(solutionStr = "", language)
    case _                            => ProgSolution(implementation = solutionStr, testData = Seq[ProgUserTestData]())
  }

}

final case class ProgUserSolution(id: Int, part: ProgExPart, solution: ProgSolution, points: Points, maxPoints: Points)
  extends UserSolution[ProgExPart, ProgSolution] {

  def language: ProgLanguage = ProgLanguages.PYTHON_3

  def commitedTestData: Seq[ProgUserTestData] = solution.testData

}

final case class ProgExerciseReview(difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview
