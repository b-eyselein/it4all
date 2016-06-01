package model.boolscheAlgebra.BFTree;

import java.util.List;

public interface BFKnoten {

	public boolean getWert();

	public List<BoolscheFormelTree> getTeilformeln(BF_Variable[] vars);
	
}
