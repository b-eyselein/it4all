package model.boolscheAlgebraTests;

import static org.junit.Assert.*;

import org.junit.Test;

import model.boolescheAlgebra.BoolescheFunktionParser;
import model.boolescheAlgebra.BFTree.BoolescheFunktionTree;

public class BFP_Test_XOR {

	@Test
	public void test_0_xor_0() {
		BoolescheFunktionTree t1 = BoolescheFunktionParser.getBFTree("a xor b");
		boolean[] b = {false , false};
		boolean wert = t1.getWert(b);
		assertFalse(wert);
	}
	
	@Test
	public void test_0_xor_1() {
		BoolescheFunktionTree t1 = BoolescheFunktionParser.getBFTree("a xor b");
		boolean[] b = {false , true};
		boolean wert = t1.getWert(b);
		assertTrue(wert);
	}
	
	@Test
	public void test_1_xor_0() {
		BoolescheFunktionTree t1 = BoolescheFunktionParser.getBFTree("a xor b");
		boolean[] b = {true , false};
		boolean wert = t1.getWert(b);
		assertTrue(wert);
	}
	
	@Test
	public void test_1_xor_1() {
		BoolescheFunktionTree t1 = BoolescheFunktionParser.getBFTree("a xor b");
		boolean[] b = {true , true};
		boolean wert = t1.getWert(b);
		assertFalse(wert);
	}
	
	// Test Vector
	@Test
	public void test_xor_vector() {
		BoolescheFunktionTree t1 = BoolescheFunktionParser.getBFTree("a xor b");
		boolean[] b = {false, true, true, false};
		boolean[] vector = t1.getWahrheitsVector();
		assertArrayEquals(b, vector);;
	}

}
