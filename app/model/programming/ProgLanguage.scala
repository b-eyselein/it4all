package model.programming

import model.Enums.Selectable
import model.programming.ProgLanguage._

import scala.language.postfixOps

object ProgLanguage {

  def STANDARD_LANG: ProgLanguage = PYTHON_3

  val values: Seq[ProgLanguage] = Seq(PYTHON_3 /*, JAVA_8*/)

  def valueOf(str: String): Option[ProgLanguage] = values find (_.name == str)

  val COMMA = ", "

  def buildToEvaluate(functionName: String, inputs: Seq[String]): String = functionName + "(" + (inputs mkString COMMA) + ")"

  val PointOfA = 97

}

trait ProgLanguage extends Selectable[ProgLanguage] {

  val name           : String
  val languageName   : String
  val aceName        : String
  val dockerImageName: String
  val fileEnding     : String
  val declaration    : String

  def buildFunction(exercise: ProgCompleteEx): String

  def buildToEvaluate(exercise: ProgCompleteEx): String

  def buildParameters(inputTypes: Seq[InputType]): String

}

object PYTHON_3 extends ProgLanguage {

  override val name           : String = "PYTHON_3"
  override val languageName   : String = "Python 3"
  override val aceName        : String = "python"
  override val dockerImageName: String = "python:3"
  override val fileEnding     : String = "py"

  override val declaration: String =
    """if __name__ == '__main__':
      |  # TODO: Solution...
      |  n = int(input())""".stripMargin

  override def buildFunction(exercise: ProgCompleteEx): String = {
    s"""def ${exercise.ex.functionName}(${buildParameters(exercise.inputTypes)}):
       |  return 0""".stripMargin
  }

  override def buildToEvaluate(exercise: ProgCompleteEx): String = exercise.ex.functionName + "(" + buildParameters(exercise.inputTypes) + ")"

  override def buildParameters(inputsTypes: Seq[InputType]): String = inputsTypes.sortBy(_.id).zipWithIndex.map {
    case (inputType, index) => (index + PointOfA).toChar
  } mkString ", "

}

//object JAVA_8 extends ProgLanguage {
//
//  override val name           : String = "JAVA_8"
//  override val languageName   : String = "Java 8"
//  override val aceName        : String = "java"
//  override val dockerImageName: String = "8-jdk"
//  override val fileEnding     : String = "java"
//
//  override val declaration: String =
//    """public class Solution
//      |
//      |  public static void main(String[] args) {
//      |    // TODO: Solution...
//      |  }
//      |
//      |}""".stripMargin
//
//  override def buildFunction(exercise: ProgCompleteEx): String =
//    s"""public int ${exercise.ex.functionName}(${inputcount2Vars(exercise.inputCount)}) {
//       |  return 0;
//       |}""".stripMargin
//
//  override def buildToEvaluate(exercise: ProgCompleteEx): String = exercise.ex.functionName + "(" + inputcount2Vars(exercise.inputCount) + ")"
//}
