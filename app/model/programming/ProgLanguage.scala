package model.programming


object ProgLanguage {

  def STANDARD_LANG: ProgLanguage = PYTHON_3

  val values: Seq[ProgLanguage] = Seq(PYTHON_3 /*, JAVA_8*/)

  def valueOf(str: String): Option[ProgLanguage] = values find (_.name == str)

  def buildToEvaluate(functionname: String, inputs: Seq[String]): String = functionname + "(" + (inputs mkString ", ") + ")"

}

trait ProgLanguage  {

  val name        : String
  val languageName: String
  val aceName     : String
  val fileEnding  : String

  def activityDiagramDisplay(exercise: ProgCompleteEx): String

  def activityDiagramDeclaration(exercise: ProgCompleteEx): String

  def buildParams(exercise: ProgCompleteEx): String

}

case object PYTHON_3 extends ProgLanguage {

  override val name        : String = "PYTHON_3"
  override val languageName: String = "Python 3"
  override val aceName     : String = "python"
  override val fileEnding  : String = "py"

  override def activityDiagramDisplay(exercise: ProgCompleteEx): String = exercise.maybeClassDiagramPart match {
    case Some(classDiagPart: UmlClassDiagPart) => classDiagPart.className + "::" + exercise.ex.functionname + buildParams(exercise)
    case None                                  => exercise.ex.functionname + buildParams(exercise)
  }

  override def activityDiagramDeclaration(exercise: ProgCompleteEx): String = "def " + exercise.ex.functionname + buildParams(exercise)

  override def buildParams(exercise: ProgCompleteEx): String = {
    val inputs = exercise.inputTypes map (it => it.inputName)

    val allInputs: Seq[String] = if (exercise.maybeClassDiagramPart.isEmpty) inputs else "self" +: inputs

    "(" + (allInputs mkString ", ") + "):"
  }

}

case object JAVA_8 extends ProgLanguage {

  override val name        : String = "JAVA_8"
  override val languageName: String = "Java 8"
  override val aceName     : String = "java"
  override val fileEnding  : String = "java"

  override def activityDiagramDisplay(exercise: ProgCompleteEx): String = ???

  override def activityDiagramDeclaration(exercise: ProgCompleteEx): String = ???

  override def buildParams(exercise: ProgCompleteEx): String =
    exercise.inputTypes sortBy (_.id) map (it => it.inputType.typeName + " " + it.inputName) mkString ", "

}
