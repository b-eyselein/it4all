package model.boolscheAlgebraTests;

import static org.junit.Assert.*;

import org.junit.Test;

import model.boolescheAlgebra.BoolescheFunktionParser;
import model.boolescheAlgebra.BFTree.BoolescheFunktionTree;

public class BFP_Test_komplexe_Formeln {

	@Test
	public void test_lange_Formel() {
		BoolescheFunktionTree t1 = BoolescheFunktionParser.getBFTree("a and b or a and c or b and not c");
		boolean[] b = {false, false, true, false, false, true, true, true};
		boolean[] vector = t1.getWahrheitsVector();
		assertArrayEquals(b, vector);
	}
	
	// Tests fuer Operatorrangfolge (wie punkt vor strich) TODO
	
	@Test
	public void test_Or_And1() {
		
		BoolescheFunktionTree t1 = BoolescheFunktionParser.getBFTree("a or b and c");
		boolean[] b = {false, true, false};
		boolean wert = t1.getWert(b);
		assertFalse(wert);
	}
	
	@Test
	public void test_Or_And2() {
		
		BoolescheFunktionTree t1 = BoolescheFunktionParser.getBFTree("(a or b) and c");
		boolean[] b = {true, false, true};
		boolean wert = t1.getWert(b);
		assertTrue(wert);
	}
	
	@Test
	public void test_Not_Or() {
		
		BoolescheFunktionTree t1 = BoolescheFunktionParser.getBFTree("not a or b");
		boolean[] b = {true, false};
		boolean wert = t1.getWert(b);
		assertFalse(wert);
	}
	
	@Test
	public void test_Not_And() {
		
		BoolescheFunktionTree t1 = BoolescheFunktionParser.getBFTree("not a and b");
		boolean[] b = {true, false};
		boolean wert = t1.getWert(b);
		assertFalse(wert);
	}
	
	// Tests fuer Exceptions:
	
	@Test (expected = IllegalArgumentException.class)
	public void testKlammerung1() {
		BoolescheFunktionParser.getBFTree("(a and b");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testKlammerung2() {
		BoolescheFunktionParser.getBFTree("a and b)");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testKlammerung3() {
		BoolescheFunktionParser.getBFTree(")a and b(");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testunvollstaendigerAusdruck1() {
		BoolescheFunktionParser.getBFTree("a or");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testunvollstaendigerAusdruck2() {
		BoolescheFunktionParser.getBFTree("a b");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testunvollstaendigerAusdruck3() {
		BoolescheFunktionParser.getBFTree("a or ()");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testunvollstaendigerAusdruck4() {
		BoolescheFunktionParser.getBFTree("a (or b)");
	}
	
	/*
	BoolFormelParser bfp = new BoolFormelParser();
	
	// should throw exception:
	//bfp.getBFTree("(      )  )   )  )"); // IllegalArgumentException
	//bfp.getBFTree("(      (  (   )  )"); // IllegalArgumentException
	//BoolscheFormelTree bf_tree_0 = bfp.getBFTree("a a a b b b b b c");
	//System.out.println(bf_tree_0.getWahrheitstafelString());
	//bfp.getBFTree("a(safd df and or not if xor not fe efg e(eg DFT) GFUIK ()  LJFSG 1  )");
	//bfp.getBFTree("1 and 1"); // IllegalArgumentException
	
	// should work:
	BoolscheFormelTree bf_tree_1 = bfp.getBFTree("       a and b or a and c          ");
	System.out.println(bf_tree_1.getWahrheitstafelString());
	BoolscheFormelTree bf_tree_2 = bfp.getBFTree(" ((( x xor o ))) ");
	System.out.println(bf_tree_2.getWahrheitstafelString());
	BoolscheFormelTree bf_tree_3 = bfp.getBFTree("      a ");
	System.out.println(bf_tree_3.getWahrheitstafelString());
	BoolscheFormelTree bf_tree_4 = bfp.getBFTree("     not a ");
	System.out.println(bf_tree_4.getWahrheitstafelString());
	BoolscheFormelTree bf_tree_5 = bfp.getBFTree("   ( not b xor a)");
	System.out.println(bf_tree_5.getWahrheitstafelString());
	BoolscheFormelTree bf_tree_6 = bfp.getBFTree("z or y and x or a and b and c and d or not d and e and not c or not a and not x");
	System.out.println(bf_tree_6.getWahrheitstafelString());
	BoolscheFormelTree bf_tree_7 = bfp.getBFTree(" ((( o xor x ))) ");
	System.out.println(bf_tree_7.getWahrheitstafelString());
	BoolscheFormelTree bf_tree_8 = bfp.getBFTree("l or e or y or b or r or q or u or y or e or v or b or x or f or k or a or p or k or q or l or h or t or n or m or m or c or b or n or y or o or h or d or e or q or j or c or a or a or p or o or o or p or a or w or e or c or r or l or q or q or f or o or u or y or a or g or s or i or m or s or t or m or h or a or u or s or c or r or p or w or o or b or p or d or b or f or n or g or o or l or q or j or q or y or p or m or v or f or c or p or v or y or z or b or v or o or k or g or v or t or s");
	System.out.println(bf_tree_8.toString());
	String[] vars = bf_tree_8.getVariablen();
	for (int i = 0; i<vars.length; i++) {
		System.out.print(vars[i]+" ");
	}
	BoolscheFormelTree bf_tree_9 = bfp.getBFTree(" ((( o and not x ))) ");
	System.out.println(bf_tree_9.getWahrheitstafelString());
	vars = bf_tree_9.getVariablen();
	for (int i = 0; i<vars.length; i++) {
		System.out.print(vars[i]+" ");
	}
	BoolscheFormelTree bf_tree_10 = bfp.getBFTree(" ((( z and y and x and w and  v and  u and  t and  s and  r and q and p and o and n and m and l and k and j and i and h and g and f and e and d and c and b and a ))) ");
	System.out.println(bf_tree_10.toString());
	vars = bf_tree_10.getVariablen();
	for (int i = 0; i<vars.length; i++) {
		System.out.print(vars[i]+" ");
	}
	BoolscheFormelTree bf_tree_11 = bfp.getBFTree(" a and b and c or not a and not b and c or a and not b and not c or not a and b and not c ");
	System.out.println(bf_tree_11.getWahrheitstafelString());
	List<BoolscheFormelTree> trees = bf_tree_11.getTeilformeln();
	for (BoolscheFormelTree bft_11 : trees) {
		System.out.println(bft_11.toString());
	}
	*/

}
