package model.boolscheAlgebra.Tests;

import static org.junit.Assert.*;

import org.junit.Test;

import model.boolscheAlgebra.BoolFormelParser;
import model.boolscheAlgebra.BFTree.BoolscheFormelTree;

public class BFP_Test_AND {
	
	@Test
	public void test_0_and_0() {
		BoolFormelParser bfp = new BoolFormelParser();
		BoolscheFormelTree t1 = bfp.getBFTree("a and b");
		boolean[] b = {false , false};
		boolean wert = t1.getWert(b);
		assertFalse(wert);
	}
	
	@Test
	public void test_0_and_1() {
		BoolFormelParser bfp = new BoolFormelParser();
		BoolscheFormelTree t1 = bfp.getBFTree("a and b");
		boolean[] b = {false , true};
		boolean wert = t1.getWert(b);
		assertFalse(wert);
	}
	
	@Test
	public void test_1_and_0() {
		BoolFormelParser bfp = new BoolFormelParser();
		BoolscheFormelTree t1 = bfp.getBFTree("a and b");
		boolean[] b = {true , false};
		boolean wert = t1.getWert(b);
		assertFalse(wert);
	}
	
	@Test
	public void test_1_and_1() {
		BoolFormelParser bfp = new BoolFormelParser();
		BoolscheFormelTree t1 = bfp.getBFTree("a and b");
		boolean[] b = {true , true};
		boolean wert = t1.getWert(b);
		assertTrue(wert);
	}
	
	// Test Vector
	@Test
	public void test_and_vector() {
		BoolFormelParser bfp = new BoolFormelParser();
		BoolscheFormelTree t1 = bfp.getBFTree("a and b");
		boolean[] b = {false, false, false, true};
		boolean[] vector = t1.getWahrheitsVector();
		assertArrayEquals(b, vector);;
	}

}