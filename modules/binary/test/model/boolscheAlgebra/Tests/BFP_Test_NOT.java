package model.boolscheAlgebra.Tests;

import static org.junit.Assert.*;

import org.junit.Test;

import model.boolscheAlgebra.BoolFormelParser;
import model.boolscheAlgebra.BFTree.BoolscheFormelTree;

public class BFP_Test_NOT {

	@Test
	public void test_not_0() {
		BoolFormelParser bfp = new BoolFormelParser();
		BoolscheFormelTree t1 = bfp.getBFTree("not a");
		boolean[] b = {false};
		boolean wert = t1.getWert(b);
		assertTrue(wert);
	}
	
	@Test
	public void test_not_1() {
		BoolFormelParser bfp = new BoolFormelParser();
		BoolscheFormelTree t1 = bfp.getBFTree("not a");
		boolean[] b = {true};
		boolean wert = t1.getWert(b);
		assertFalse(wert);
	}
	
	// Test Vector
		@Test
		public void test_not_vector() {
			BoolFormelParser bfp = new BoolFormelParser();
			BoolscheFormelTree t1 = bfp.getBFTree("not a");
			boolean[] b = {true, false};
			boolean[] vector = t1.getWahrheitsVector();
			assertArrayEquals(b, vector);;
		}

}
