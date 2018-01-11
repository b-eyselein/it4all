package model.programming

import java.nio.file.attribute.{PosixFilePermission, PosixFilePermissions}

import controllers.exes.idExes.ProgToolObject
import model.User
import model.core.{FileUtils, GenericCompleteResult}
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object ProgLangCorrector extends FileUtils {

  val ResultFile = "result.txt"

  val TestdataFile = "testdata.txt"

  val FilePermissions: java.util.Set[PosixFilePermission] = PosixFilePermissions.fromString("rwx------")

  val ResultFileName = "result"

  def toAddPython(ex: ProgExercise, language: ProgLanguage): String = {
    val inputsAsVars = ('a' to 'z') take ex.inputCount map (_.toString)

    val toEvaluate = ProgLanguage.buildToEvaluate(ex.functionName, inputsAsVars)

    s"""if __name__ == "__main__":
       |  import json
       |
       |  (index, ${inputsAsVars mkString ", "}) = map(int, input().split(' '))
       |
       |  toWrite = {"inputs": [${inputsAsVars map (ip => s"""{"variable": "$ip", "value": $ip}""") mkString ", "}], "functionName": "${ex.functionName}"}
       |
       |  result = $toEvaluate
       |
       |  toWrite["result"] = result
       |
       |  file = open("$ResultFileName{}.txt".format(index), 'w')
       |  file.write(json.dumps(toWrite, indent = 2))
       |""".stripMargin
  }

  def correct(user: User, exercise: ProgCompleteEx, learnerSolution: String, language: ProgLanguage)(implicit ec: ExecutionContext): Future[GenericCompleteResult[ProgEvaluationResult]] = {

    val futureImageExists: Future[Boolean] = Future(DockerConnector.imageExists(language.dockerImageName) || DockerConnector.pullImage(language.dockerImageName))

    val targetDir = ProgToolObject.solutionDirForExercise(user.username, exercise.ex)

    val solutionPath = targetDir / s"solution.${language.fileEnding}"

    val testData = exercise.sampleTestData

    write(solutionPath, learnerSolution + "\n\n" + toAddPython(exercise.ex, language))

    write(targetDir, TestdataFile, (testData sortBy (_.sampleTestData.id) map (_.write) mkString "\n") + "\n")

    copy(language.aceName + ".sh", ProgToolObject.exerciseResourcesFolder, targetDir)

    // Check if image exists
    futureImageExists flatMap {
      _ => DockerConnector.runContainer(language, targetDir)
    } map {
      _ =>
        val results = testData.zipWithIndex map {
          case (td, index) =>

            val outputFilePath = targetDir / s"output$index.txt"
            val errorFilePath = targetDir / s"error$index.txt"
            val resultFilePath = targetDir / s"$ResultFileName$index.txt"

            val maybeOutput: Option[String] = if (outputFilePath.toFile.exists)
              readAll(outputFilePath) map Some.apply getOrElse None
            else None

            if (errorFilePath.toFile.exists()) {

              readAll(errorFilePath) match {
                case Failure(error)   => ProgEvaluationResult(EvaluationFailed(error), td)
                case Success(content) => ProgEvaluationResult(SyntaxError("", content, maybeOutput), td)
              }

            } else if (resultFilePath.toFile.exists()) {

              readAll(resultFilePath) match {
                case Failure(error)   => ProgEvaluationResult(EvaluationFailed(error), td)
                case Success(content) =>
                  val res = ExecutionResult.fromJson(Json.parse(content), maybeOutput) getOrElse EvaluationFailed(new Exception("Reading of following content failed:\n" + content))
                  ProgEvaluationResult(res, td)
              }

            } else {
              ProgEvaluationResult(EvaluationFailed(new Exception("Allgemeiner Fehler!")), td)
            }
        }

        GenericCompleteResult(learnerSolution, results)
    }
  }

}