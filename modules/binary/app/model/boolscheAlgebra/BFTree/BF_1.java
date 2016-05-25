package model.boolscheAlgebra.BFTree;

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
	public List<BoolscheFormelTree> getTeilformeln(BF_Variable[] vars) {
		return new ArrayList<BoolscheFormelTree>();
	}

}
