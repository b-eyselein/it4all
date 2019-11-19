package model.tools.programming.persistence

import model._
import model.persistence._
import model.points.Points
import model.tools.programming._
import model.tools.uml.UmlClassDiagram
import play.api.libs.json.{JsArray, JsValue}

object ProgDbModels extends ADbModels[ProgExercise, DbProgExercise] {

  override def dbExerciseFromExercise(ex: ProgExercise): DbProgExercise = DbProgExercise(
    ex.id, ex.collectionId, ex.semanticVersion, ex.title, ex.author, ex.text, ex.state,
    ex.functionName, ex.foldername, ex.filename,
    ex.inputTypes, ex.outputType,
    ex.baseData,
    ex.unitTestPart,
    ex.implementationPart.base, ex.implementationPart.implFileName, ex.implementationPart.sampleSolFileNames,
    ex.tags,
    ex.sampleTestData,
    ex.maybeClassDiagramPart
  )

  def exerciseFromDbValues(dbProgEx: DbProgExercise, sampleSolutions: Seq[ProgSampleSolution], implementationFiles: Seq[ExerciseFile]): ProgExercise = dbProgEx match {
    case DbProgExercise(
    id, collectionId, semanticVersion, title, author, text, state,
    functionname, foldername, filename,
    inputTypes, outputType,
    baseData,
    unitTestPart,
    implementationBase, implFileName, implementationSampleSolFileNames,
    tags,
    sampleTestData,
    maybeClassDiagramPart
    ) =>

      ProgExercise(
        id, collectionId, semanticVersion, title, author, text, state,
        functionname, foldername, filename,
        inputTypes, outputType,
        baseData,
        unitTestPart,
        ImplementationPart(implementationBase, implementationFiles, implFileName, implementationSampleSolFileNames),
        sampleSolutions, sampleTestData,
        tags,
        maybeClassDiagramPart
      )
  }

  // User Test Data

  def dbUserTestDataFromUserTestData(exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, testData: ProgUserTestData): DbProgUserTestData =
    DbProgUserTestData(testData.id, exId, collId, username, testData.input, testData.output, testData.state)

  def userTestDataFromDbUserTestData(dbTestData: DbProgUserTestData): ProgUserTestData =
    ProgUserTestData(dbTestData.id, dbTestData.inputAsJson, dbTestData.output, dbTestData.state)

}

object ProgSolutionDbModels /* extends ASolutionDbModels[ProgSolution, ProgExPart, ProgSampleSolution, DbProgSampleSolution, ProgUserSolution, DbProgUserSolution] */ {

  def progTestDataToJson(testData: Seq[ProgUserTestData]): JsValue = JsArray() // FIXME: implement... ???

  def testDataFromJson(jsValue: JsValue): Seq[ProgUserTestData] = Seq.empty // FIXME: implement... ???

  // Sample solutions

  /* override */ def dbSampleSolFromSampleSol(exId: Int, exSemVer: SemanticVersion, collId: Int, sample: ProgSampleSolution): (DbProgSampleSolution, Seq[DbProgSampleSolutionFile]) = sample match {
    case ProgSampleSolution(id, ProgSolution(files, _)) =>

      // FIXME: save testData?

      val dbProgSampleSolutionFiles = files.map { case ExerciseFile(name, content, fileType, editable) =>
        DbProgSampleSolutionFile(name, id, exId, collId, content, fileType, editable)
      }

      (DbProgSampleSolution(id, exId, collId), dbProgSampleSolutionFiles)
  }

  /* override */ def sampleSolFromDbSampleSol(dbSample: DbProgSampleSolution, dbSampleFiles: Seq[DbProgSampleSolutionFile]): ProgSampleSolution = {

    val files: Seq[ExerciseFile] = dbSampleFiles.map { case DbProgSampleSolutionFile(fileName, _, _, _, fileContent, fileType, fileIsEditable) =>
      ExerciseFile(fileName, fileContent, fileType, fileIsEditable)
    }

    val testData: Seq[ProgUserTestData] = Seq.empty // ???

    ProgSampleSolution(dbSample.id, ProgSolution(files, testData))
  }

  // User solutions

  /* override */ def dbUserSolFromUserSol(exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, solution: ProgUserSolution): (DbProgUserSolution, Seq[DbProgUserSolutionFile]) =
    solution match {
      case ProgUserSolution(id, part, ProgSolution(files, testData), points, maxPoints) =>

        val dbProgUserSolution = DbProgUserSolution(id, exId, collId, username, part, progTestDataToJson(testData), points, maxPoints)

        val dbFiles = files.map { case ExerciseFile(name, content, fileType, editable) =>
          DbProgUserSolutionFile(name, id, exId, collId, username, part, content, fileType, editable)
        }

        (dbProgUserSolution, dbFiles)
    }

  /* override */ def userSolFromDbUserSol(dbSol: DbProgUserSolution, dbSolFiles: Seq[DbProgUserSolutionFile]): ProgUserSolution = {

    val files                    = dbSolFiles.map { case DbProgUserSolutionFile(fileName, _, _, _, _, _, fileContent, fileType, fileIsEditable) =>
      ExerciseFile(fileName, fileContent, fileType, fileIsEditable)
    }
    val dbSolution: ProgSolution = ProgSolution(files, testData = Seq.empty) // FIXME: testData?

    ProgUserSolution(dbSol.id, dbSol.part, dbSolution, dbSol.points, dbSol.maxPoints)
  }

}

object ProgExerciseReviewDbModels extends AExerciseReviewDbModels[ProgExPart, ProgExerciseReview, DbProgrammingExerciseReview] {

  override def dbReviewFromReview(username: String, collId: Int, exId: Int, part: ProgExPart, review: ProgExerciseReview): DbProgrammingExerciseReview =
    DbProgrammingExerciseReview(username, collId, exId, part, review.difficulty, review.maybeDuration)

  override def reviewFromDbReview(dbReview: DbProgrammingExerciseReview): ProgExerciseReview =
    ProgExerciseReview(dbReview.difficulty, dbReview.maybeDuration)

}

final case class DbProgExercise(
  id: Int, collectionId: Int, semanticVersion: SemanticVersion, title: String, author: String, text: LongText, state: ExerciseState,
  functionname: String, foldername: String, filename: String,
  inputType: Seq[ProgInput], outputType: ProgDataType,
  baseData: Option[JsValue],
  unitTestPart: UnitTestPart,
  implementationBase: String, implFileName: String, implementationSampleSolFileNames: Seq[String],
  tags: Seq[ProgrammingExerciseTag],
  sampleTestData: Seq[ProgSampleTestData],
  maybeClassDiagramPart: Option[UmlClassDiagram]
) extends ADbExercise


final case class DbProgSampleSolution(id: Int, exId: Int, collId: Int) extends ADbSampleSol {

  // FIXME: remove!
  val exSemVer: SemanticVersion = SemanticVersionHelper.DEFAULT

  //  val sample = ProgSolution(
  //    sampleStr, testData = Seq[ProgUserTestData](),
  //    unitTest = Some(ExerciseFile(unitTestFileName, unitTestFileContent, unitTestFileType, unitTestFileIsEditable))
  //  )

}

final case class DbProgSampleSolutionFile(
  fileName: String, solId: Int, exId: Int, collId: Int,
  fileContent: String, fileType: String, fileIsEditable: Boolean
)

final case class DbProgUserSolution(
  id: Int, exId: Int, collId: Int, username: String, part: ProgExPart,
  //  implementation: String,
  //  unitTestFileName: String, unitTestFileContent: String, unitTestFileType: String, unitTestFileIsEditable: Boolean,
  testData: JsValue,
  points: Points, maxPoints: Points
) extends ADbUserSol[ProgExPart] {

  // FIXME: remove!
  val exSemVer: SemanticVersion = SemanticVersionHelper.DEFAULT

  //  val solution: ProgSolution = ProgSolution(
  //    implementation,
  //    ProgSolutionDbModels.testDataFromJson(testData),
  //    Some(ExerciseFile(unitTestFileName, unitTestFileContent, unitTestFileType, unitTestFileIsEditable))
  //  )

}

final case class DbProgUserSolutionFile(
  fileName: String, solId: Int, exId: Int, collId: Int, username: String, part: ProgExPart,
  fileContent: String, fileType: String, fileIsEditable: Boolean
)

final case class DbProgUserTestData(id: Int, exId: Int, collId: Int, username: String, inputAsJson: JsValue, output: JsValue, state: ExerciseState)

// Exercise Review

final case class DbProgrammingExerciseReview(
  username: String, collId: Int, exerciseId: Int, exercisePart: ProgExPart, difficulty: Difficulty, maybeDuration: Option[Int])
  extends DbExerciseReview[ProgExPart]
