package model.programming

import scala.collection.mutable.ListBuffer

object AvailableLanguages {
  var values: ListBuffer[AvailableLanguages] = ListBuffer.empty

  def byName(name: String): Option[AvailableLanguages] = values.find(_.toString == name)

  def stdLang: AvailableLanguages = PYTHON_3
}

abstract sealed class AvailableLanguages(val name: String, val aceName: String, val imageName: String, val scriptName: String, val declaration: String) {
  AvailableLanguages.values += this
}


case object PYTHON_3 extends AvailableLanguages("Python 3", "python", "python:3", "sol.py",
  """if __name__ == '__main__':
    |  # TODO: Solution...
    |  n = int(input())""".stripMargin)

case object JAVA_8 extends AvailableLanguages("Java 8", "java", "8-jdk", "sol.java",
  """public class Solution
    |
    |  public static void main(String[] args) {
    |    // TODO: Solution...
    |  }
    |
    |}""".stripMargin)
