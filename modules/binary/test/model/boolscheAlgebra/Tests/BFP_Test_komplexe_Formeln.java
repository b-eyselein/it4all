package model.boolscheAlgebra.Tests;

import static org.junit.Assert.*;

import org.junit.Test;

import model.boolscheAlgebra.BoolFormelParser;
import model.boolscheAlgebra.BFTree.BoolscheFormelTree;

public class BFP_Test_komplexe_Formeln {

	@Test
	public void test1() {
		BoolFormelParser bfp = new BoolFormelParser();
		BoolscheFormelTree t1 = bfp.getBFTree("a and b or a and c or b and not c");
		boolean[] b = {false, false, true, false, false, true, true, true};
		boolean[] vector = t1.getWahrheitsVector();
		assertArrayEquals(b, vector);
	}

}
