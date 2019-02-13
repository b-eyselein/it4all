package model.programming

import enumeratum.{EnumEntry, PlayEnum}

import scala.collection.immutable.IndexedSeq


sealed abstract class ProgLanguage(val languageName: String, val aceName: String, val fileEnding: String) extends EnumEntry {

  def activityDiagramDisplay(exercise: ProgCompleteEx): String

  def activityDiagramDeclaration(exercise: ProgCompleteEx): String

  def buildParams(exercise: ProgCompleteEx): String

}

object ProgLanguages extends PlayEnum[ProgLanguage] {

  def StandardLanguage: ProgLanguage = PYTHON_3

  val values: IndexedSeq[ProgLanguage] = findValues

  def buildToEvaluate(functionname: String, inputs: Seq[String]): String = functionname + "(" + (inputs mkString ", ") + ")"


  case object PYTHON_3 extends ProgLanguage("Python 3", "python", "py") {

    override def activityDiagramDisplay(exercise: ProgCompleteEx): String = exercise.maybeClassDiagramPart match {
      case None => exercise.functionName + buildParams(exercise)
    }

    override def activityDiagramDeclaration(exercise: ProgCompleteEx): String = "def " + exercise.functionName + buildParams(exercise)

    override def buildParams(exercise: ProgCompleteEx): String = {
      val inputs = exercise.inputTypes map (it => it.inputName)

      val allInputs: Seq[String] = if (exercise.maybeClassDiagramPart.isEmpty) inputs else "self" +: inputs

      "(" + (allInputs mkString ", ") + "):"
    }

  }

  case object JAVA_8 extends ProgLanguage("Java 8", "java", "java") {

    override def activityDiagramDisplay(exercise: ProgCompleteEx): String = ???

    override def activityDiagramDeclaration(exercise: ProgCompleteEx): String = ???

    override def buildParams(exercise: ProgCompleteEx): String =
      exercise.inputTypes sortBy (_.id) map (it => it.inputType.typeName + " " + it.inputName) mkString ", "

  }

}
