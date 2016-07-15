package model.boolescheAlgebra.BFTree;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

public class AssignmentTest {
  
  @Test
  public void testGenerateAllAssignments() {
    // TODO!
    char[] variables = {'a', 'b', 'c'};
    List<Assignment> assignments = Assignment.generateAllAssignments(variables);
    assertNotNull(assignments);
    // fail("Not yet implemented");
  }
  
  @Test
  public void testGetAssignment() {
    // TODO!
    // fail("Not yet implemented");
  }
  
  @Test
  public void testSetAssignment() {
    // TODO!
    // fail("Not yet implemented");
  }
  
  @Test
  public void testToString() {
    // TODO!
    // fail("Not yet implemented");
  }
  
}
