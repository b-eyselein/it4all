package model.boolescheAlgebra.BFTree;

import static org.junit.Assert.*;

import org.junit.Test;

public class BoolescheFunktionTreeTest {

  @Test
  public void testBoolescheFunktionTree() {
    // fail("Not yet implemented");
  }

  @Test
  public void testCompareBooleanArray() {
    // fail("Not yet implemented");
  }

  @Test
  /**
   * Test that a == (a and b) or (a and not b)
   */
  public void testCompareBoolscheFormelTree() {
    Variable a1 = new Variable("a"), b1 = new Variable("b");
    Not notb = new Not(b1);

    BoolescheFunktionTree tree1 = new BoolescheFunktionTree(a1, a1);

    And and1 = new And(a1, b1);
    And and2 = new And(a1, notb);
    Or or = new Or(and1, and2);

    BoolescheFunktionTree tree2 = new BoolescheFunktionTree(or, a1, b1);

    assertTrue("Bäume sollten gleich sein, sind es aber nicht!", tree1.compareBoolscheFormelTree(tree2));

  }

  @Test
  public void testCompareStringArray() {
    // fail("Not yet implemented");
  }

  @Test
  public void testGetAnzahlVariablen() {
    // fail("Not yet implemented");
  }

  @Test
  public void testGetTeilformeln() {
    // fail("Not yet implemented");
  }

  @Test
  public void testGetVariablen() {
    // fail("Not yet implemented");
  }

  @Test
  public void testGetVariablenTabelle() {
    // fail("Not yet implemented");
  }

  @Test
  public void testGetWahrheitstafelBoolean() {
    // fail("Not yet implemented");
  }

  @Test
  public void testGetWahrheitstafelChar() {
    // fail("Not yet implemented");
  }

  @Test
  public void testGetWahrheitstafelString() {
    // fail("Not yet implemented");
  }

  @Test
  public void testGetWahrheitsVector() {
    // fail("Not yet implemented");
  }

  @Test
  public void testGetWahrheitsVectorChar() {
    // fail("Not yet implemented");
  }

  @Test
  public void testGetWert() {
    // fail("Not yet implemented");
  }

  @Test
  public void testToString() {
    // fail("Not yet implemented");
  }

}
