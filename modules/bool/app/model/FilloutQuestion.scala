package model;

class FilloutQuestion(val formula: ScalaNode) extends BooleanQuestion(formula.usedVariables.toList) {

  val assignments: List[Assignment] = Assignment.generateAllAssignments(formula.usedVariables.toList)

  val HTML_REPLACERS = Map("IMPL" -> "&rArr;", "NOR" -> "&#x22bd;", "NAND" -> "&#x22bc;", "EQUIV" -> "&hArr;", "NOT" -> "&not;",
    "AND" -> "&and;", "XOR" -> "&oplus;", "OR" -> "&or;")

  def getAssignments = assignments

  def getFormulaAsHtml(): String = {
    var formulaAsHtml = getFormulaAsString

    for ((key, value) <- HTML_REPLACERS)
      formulaAsHtml = formulaAsHtml.replaceAll(key, value);

    formulaAsHtml;
  }

  def getFormulaAsString() = formula.getAsString(false)

  def isCorrect() = assignments.forall((a) => {
    a.isSet(BooleanQuestion.LEARNER_VARIABLE) && a.get(BooleanQuestion.LEARNER_VARIABLE) == a.get(BooleanQuestion.SOLUTION_VARIABLE)
  })
}

object FilloutQuestion {
  def generateNew() = new FilloutQuestion(BoolFormulaGenerator.generateRandom());
}
