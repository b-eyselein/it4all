package model.tools.flask

import model.points._
import model.tools.AbstractCorrector
import model.tools.flask.FlaskTool.solutionDirForExercise
import model.{Exercise, FilesSolution, points}
import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}

object FlaskCorrector extends AbstractCorrector {

  override protected val logger: Logger = Logger(this.getClass)

  override type AbstractResult = FlaskAbstractCorrectionResult

  override protected def buildInternalError(msg: String, maxPoints: points.Points): FlaskInternalErrorResult =
    FlaskInternalErrorResult(msg, maxPoints)

  def correct(
    username: String,
    solution: FilesSolution,
    exercise: Exercise[FlaskExerciseContent]
  )(implicit executionContext: ExecutionContext): Future[FlaskAbstractCorrectionResult] = Future {

    val maxPoints = exercise.content.maxPoints.points

    val solTargetDir = solutionDirForExercise(username, exercise.collectionId, exercise.exerciseId)

    // Write solution files
    solution.files.map { f =>
      val targetFile = solTargetDir / f.name

      targetFile
        .createIfNotExists(createParents = true)
        .write(f.content)
    }

    FlaskInternalErrorResult("Correction is not yet implemented!", maxPoints)
  }

}
