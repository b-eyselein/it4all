package model.boolescheAlgebra.BFTree;

import java.util.ArrayList;
import java.util.List;

public class BF_1 implements BFKnoten {
	
	@Override
	public boolean getWert() {
		return true;
	}
	
	@Override
	public String toString() {
		return "1";
	}

	@Override
	public List<BoolescheFunktionTree> getTeilformeln(BF_Variable[] vars) {
		return new ArrayList<BoolescheFunktionTree>();
	}

}
