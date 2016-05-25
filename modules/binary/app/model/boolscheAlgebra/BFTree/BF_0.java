package model.boolscheAlgebra.BFTree;

import java.util.ArrayList;
import java.util.List;

public class BF_0 implements BFKnoten {
	
	@Override
	public boolean getWert() {
		return false;
	}
	
	@Override
	public String toString() {
		return "0";
	}

	@Override
	public List<BoolscheFormelTree> getTeilformeln(BF_Variable[] vars) {
		return new ArrayList<BoolscheFormelTree>();
	}

}
