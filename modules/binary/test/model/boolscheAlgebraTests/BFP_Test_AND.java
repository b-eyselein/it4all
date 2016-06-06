package model.boolscheAlgebraTests;

import static org.junit.Assert.*;

import org.junit.Test;

import model.boolescheAlgebra.BoolescheFunktionParser;
import model.boolescheAlgebra.BFTree.BoolescheFunktionTree;

public class BFP_Test_AND {
	
	@Test
	public void test_0_and_0() {
		BoolescheFunktionTree t1 = BoolescheFunktionParser.getBFTree("a and b");
		boolean[] b = {false , false};
		boolean wert = t1.getWert(b);
		assertFalse(wert);
	}
	
	@Test
	public void test_0_and_1() {
		BoolescheFunktionTree t1 = BoolescheFunktionParser.getBFTree("a and b");
		boolean[] b = {false , true};
		boolean wert = t1.getWert(b);
		assertFalse(wert);
	}
	
	@Test
	public void test_1_and_0() {
		BoolescheFunktionTree t1 = BoolescheFunktionParser.getBFTree("a and b");
		boolean[] b = {true , false};
		boolean wert = t1.getWert(b);
		assertFalse(wert);
	}
	
	@Test
	public void test_1_and_1() {
		BoolescheFunktionTree t1 = BoolescheFunktionParser.getBFTree("a and b");
		boolean[] b = {true , true};
		boolean wert = t1.getWert(b);
		assertTrue(wert);
	}
	
	// Test Vector
	@Test
	public void test_and_vector() {
		BoolescheFunktionTree t1 = BoolescheFunktionParser.getBFTree("a and b");
		boolean[] b = {false, false, false, true};
		boolean[] vector = t1.getWahrheitsVector();
		assertArrayEquals(b, vector);;
	}

}
