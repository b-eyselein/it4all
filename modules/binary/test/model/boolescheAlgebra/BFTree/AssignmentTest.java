package model.boolescheAlgebra.BFTree;

import java.util.List;

import org.junit.Test;

public class AssignmentTest {
  
  @Test
  public void testGenerateAllAssignments() {
    char[] variables = {'a', 'b', 'c'};
    List<Assignment> assignments = Assignment.generateAllAssignments(variables);
    for(Assignment a: assignments)
      System.out.println(a);
    // fail("Not yet implemented");
  }
  
  @Test
  public void testGetAssignment() {
    // fail("Not yet implemented");
  }
  
  @Test
  public void testSetAssignment() {
    // fail("Not yet implemented");
  }
  
  @Test
  public void testToString() {
    // fail("Not yet implemented");
  }
  
}
