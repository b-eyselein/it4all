package model.tools.collectionTools.programming

import enumeratum.{EnumEntry, PlayEnum}

sealed abstract class ProgLanguage(val languageName: String, val aceName: String, val fileEnding: String)
    extends EnumEntry {

  def activityDiagramDisplay(exercise: ProgExerciseContent): String

  def activityDiagramDeclaration(exercise: ProgExerciseContent): String

  def buildParams(exercise: ProgExerciseContent): String

}

object ProgLanguages extends PlayEnum[ProgLanguage] {

  def StandardLanguage: ProgLanguage = PYTHON_3

  val values: IndexedSeq[ProgLanguage] = findValues

  def buildToEvaluate(functionname: String, inputs: Seq[String]): String =
    functionname + "(" + (inputs mkString ", ") + ")"

  case object PYTHON_3 extends ProgLanguage("Python 3", "python", "py") {

    override def activityDiagramDisplay(exercise: ProgExerciseContent): String = exercise.maybeClassDiagramPart match {
      case Some(_) => ???
      case None    => exercise.functionName + buildParams(exercise)
    }

    override def activityDiagramDeclaration(exercise: ProgExerciseContent): String =
      "def " + exercise.functionName + buildParams(exercise)

    override def buildParams(exercise: ProgExerciseContent): String = {
      val inputs = exercise.inputTypes map (it => it.inputName)

      val allInputs: Seq[String] = if (exercise.maybeClassDiagramPart.isEmpty) inputs else "self" +: inputs

      "(" + (allInputs mkString ", ") + "):"
    }

  }

  //  case object JAVA_8 extends ProgLanguage("Java 8", "java", "java") {
  //
  //    override def activityDiagramDisplay(exercise: ProgExercise): String = ???
  //
  //    override def activityDiagramDeclaration(exercise: ProgExercise): String = ???
  //
  //    override def buildParams(exercise: ProgExercise): String =
  //      exercise.inputTypes sortBy (_.id) map (it => it.inputType.typeName + " " + it.inputName) mkString ", "
  //
  //  }

}
