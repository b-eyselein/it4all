package model.boolescheAlgebra.BFTree;

import java.util.List;

public interface BFKnoten {

	public boolean getWert();

	public List<BoolescheFunktionTree> getTeilformeln(BF_Variable[] vars);
	
}
