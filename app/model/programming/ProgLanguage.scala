package model.programming

import model.Enums.Selectable
import model.programming.ProgLanguage._

object ProgLanguage {

  val STANDARD_LANG: ProgLanguage = PYTHON_3

  val values: List[ProgLanguage] = List(PYTHON_3, JAVA_8)

  def valueOf(str: String): Option[ProgLanguage] = values find (_.name == str)

  def inputcount2Vars(ic: Int): List[Char] = ('a' to 'z').take(ic).toList

}

abstract class ProgLanguage(val name: String, val languageName: String, val aceName: String, val dockerImageName: String,
                            val scriptEnding: String)
  extends Selectable[ProgLanguage] {

  val declaration: String

  def buildFunction(name: String, inputs: Int): String

}

case object PYTHON_3 extends ProgLanguage("PYTHON_3", "Python 3", "python", "python:3", "py") {

  override val declaration: String =
    """if __name__ == '__main__':
      |  # TODO: Solution...
      |  n = int(input())""".stripMargin

  override def buildFunction(name: String, inputs: Int): String =
    s"""def $name(${inputcount2Vars(inputs).mkString(", ")}):
       |  return 0""".stripMargin

}

case object JAVA_8 extends ProgLanguage("JAVA_8", "Java 8", "java", "8-jdk", "java") {

  override val declaration: String =
    """public class Solution
      |
      |  public static void main(String[] args) {
      |    // TODO: Solution...
      |  }
      |
      |}""".stripMargin

  override def buildFunction(name: String, inputs: Int): String =
    s"""public void $name(${inputcount2Vars(inputs).mkString(", ")}) {
       |  return 0;
       |}
     """.stripMargin
}
