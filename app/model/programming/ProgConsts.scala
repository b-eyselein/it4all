package model.programming

import model.Consts

object ProgConsts extends Consts {

  val AWAITED_VALUE_NAME = "awaitedValue"

  val FUNCTIONNAME_NAME = "functionName"

  val INPUTCOUNT_NAME = "inputCount"
  val INPUT_NAME      = "input"
  val INPUTS_NAME     = "inputs"

  val LANGUAGE_NAME = "language"

  val OUTPOUT_NAME = "output"

  val SAMPLE_NAME          = "sample"
  val SAMPLE_SOLS_NAME     = "sampleSolutions"
  val SAMPLE_TESTDATA_NAME = "sampleTestData"

  val PYTHON_DEFAULT: String =
    """if __name__ == '__main__':
      |  # TODO: Solution...
      |  n = int(input())""".stripMargin

  val JAVA_DEFAULT: String =
    """public class Solution
      |
      |  public static void main(String[] args) {
      |    // TODO: Solution...
      |  }
      |
      |}""".stripMargin

}
