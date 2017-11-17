package model.essentials

class BoolAssignment(assignments: Map[Variable, Boolean]) {

  val SOL_VAR: Variable = BooleanQuestion.SOLUTION_VARIABLE
  val LEA_VAR: Variable = BooleanQuestion.LEARNER_VARIABLE

  def asChar(variable: Variable): Char = if (apply(variable)) '1' else '0'

  def asChar(char: Char): Char = if (apply(Variable(char))) '1' else '0'

  def apply(variable: Variable) = assignments(variable)

  def isCorrect: Boolean = apply(LEA_VAR) == apply(SOL_VAR)

  def getColor: String = if (isCorrect) "success" else "danger"

  def getLearnerValueAsChar: Char = asChar(LEA_VAR)

  def variables: Iterable[Variable] = assignments.keys.filter(variable => variable != SOL_VAR && variable != LEA_VAR)

  def isSet(variable: Variable): Boolean = assignments.isDefinedAt(variable)

  override def toString: String = assignments
    .filter(as => as._1 != LEA_VAR && as._1 != SOL_VAR)
    .map(as => as._1 + ":" + (if (as._2) "1" else "0"))
    .mkString(",")

  def +(toAssign: (Variable, Boolean)) = new BoolAssignment(assignments = assignments + (toAssign._1 -> toAssign._2))
}

object BoolAssignment {
  //  implicit def intToBool(value: Int): Boolean = value != 0

  def apply(assigns: (Variable, Boolean)*) = new BoolAssignment(assigns.toMap)

  def generateAllAssignments(variables: List[Variable]): List[BoolAssignment] = variables.sorted.reverse match {
    case Nil          => List.empty
    case head :: Nil  => List(BoolAssignment(head -> false), BoolAssignment(head -> true))
    case head :: tail =>
      val falseAssignments, trueAssignments = generateAllAssignments(tail)
      falseAssignments.map(_ + (head -> false)) ++ trueAssignments.map(_ + (head -> true))

  }

  def getNF(assignments: List[BoolAssignment], takePos: Boolean, innerF: (ScalaNode, ScalaNode) => ScalaNode, outerF: (ScalaNode, ScalaNode) => ScalaNode): ScalaNode = assignments
    .filter(takePos ^ _ (BooleanQuestion.SOLUTION_VARIABLE))
    .map(as => as.variables.map(v => if (takePos ^ as(v)) v else ScalaNode.not(v)).reduceLeft(innerF)) match {
    case Nil => ScalaNode.constant(takePos)
    case l   => l.reduceLeft(outerF)
  }

  def getDisjunktiveNormalForm(assignments: List[BoolAssignment]): ScalaNode = getNF(assignments, takePos = false, AndScalaNode, OrScalaNode)

  def getKonjunktiveNormalForm(assignments: List[BoolAssignment]): ScalaNode = getNF(assignments, takePos = true, OrScalaNode, AndScalaNode)
}
