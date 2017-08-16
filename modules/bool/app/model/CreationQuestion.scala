package model;

import scala.collection.JavaConverters._

case class CreationQuestion(vars: List[Variable], val solutions: List[Assignment]) extends BooleanQuestion(vars) {

  def getSolutionVariable() = BooleanQuestion.SOLUTION_VARIABLE

}

object CreationQuestion {
  def generateNew(): CreationQuestion = {
    null
    //    // Get two or three variables a, b (and c)
    //    int anzVars = GENERATOR.nextInt(2) + 2;
    //    Set<Character> variables = IntStream.range('a', 'a' + anzVars).mapToObj(i => (char) i).collect(Collectors.toSet());
    //    
    //    // Generate random solutions for all assignments
    //    List<Assignment> assignments = Assignment.generateAllAssignments(variables);
    //    assignments.forEach(a => a.setAssignment(SOLUTION_VARIABLE, GENERATOR.nextBoolean()));
    //    
    //    return new CreationQuestion(variables, assignments);
  }
}
