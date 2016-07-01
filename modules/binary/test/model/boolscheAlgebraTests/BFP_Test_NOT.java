package model.boolscheAlgebraTests;

import static org.junit.Assert.*;

import org.junit.Test;

import model.boolescheAlgebra.BoolescheFunktionParser;
import model.boolescheAlgebra.BFTree.BoolescheFunktionTree;

public class BFP_Test_NOT {

	@Test
	public void test_not_0() {
		BoolescheFunktionTree t1 = BoolescheFunktionParser.parse("not a");
		boolean[] b = {false};
		boolean wert = t1.getWert(b);
		assertTrue(wert);
	}
	
	@Test
	public void test_not_1() {
		BoolescheFunktionTree t1 = BoolescheFunktionParser.parse("not a");
		boolean[] b = {true};
		boolean wert = t1.getWert(b);
		assertFalse(wert);
	}
	
	// Test Vector
	@Test
	public void test_not_vector() {
		BoolescheFunktionTree t1 = BoolescheFunktionParser.parse("not a");
		boolean[] b = {true, false};
		boolean[] vector = t1.getWahrheitsVector();
		assertArrayEquals(b, vector);;
	}

}
