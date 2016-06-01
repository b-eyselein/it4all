package model.boolscheAlgebra.Tests;

import static org.junit.Assert.*;

import org.junit.Test;

import model.boolscheAlgebra.BoolFormelParser;
import model.boolscheAlgebra.BFTree.BoolscheFormelTree;

public class BFP_Test_XOR {

	@Test
	public void test_0_xor_0() {
		BoolFormelParser bfp = new BoolFormelParser();
		BoolscheFormelTree t1 = bfp.getBFTree("a xor b");
		boolean[] b = {false , false};
		boolean wert = t1.getWert(b);
		assertFalse(wert);
	}
	
	@Test
	public void test_0_xor_1() {
		BoolFormelParser bfp = new BoolFormelParser();
		BoolscheFormelTree t1 = bfp.getBFTree("a xor b");
		boolean[] b = {false , true};
		boolean wert = t1.getWert(b);
		assertTrue(wert);
	}
	
	@Test
	public void test_1_xor_0() {
		BoolFormelParser bfp = new BoolFormelParser();
		BoolscheFormelTree t1 = bfp.getBFTree("a xor b");
		boolean[] b = {true , false};
		boolean wert = t1.getWert(b);
		assertTrue(wert);
	}
	
	@Test
	public void test_1_xor_1() {
		BoolFormelParser bfp = new BoolFormelParser();
		BoolscheFormelTree t1 = bfp.getBFTree("a xor b");
		boolean[] b = {true , true};
		boolean wert = t1.getWert(b);
		assertFalse(wert);
	}
	
	// Test Vector
		@Test
		public void test_xor_vector() {
			BoolFormelParser bfp = new BoolFormelParser();
			BoolscheFormelTree t1 = bfp.getBFTree("a xor b");
			boolean[] b = {false, true, true, false};
			boolean[] vector = t1.getWahrheitsVector();
			assertArrayEquals(b, vector);;
	}

}
