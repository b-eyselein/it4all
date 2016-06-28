package model.boolescheAlgebra.BFTree;

import java.util.ArrayList;
import java.util.List;

public class BF_Variable implements BFKnoten {
	
	private String name;
	private boolean wert;
	
	public BF_Variable(String s) {
		this.name = s;
	}
	
	public void setWert(boolean b) {
		this.wert = b;
	}
	
	@Override
	public boolean getWert() {
		return this.wert;
	}
	
	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public List<BoolescheFunktionTree> getTeilformeln(BF_Variable[] vars) {
		return new ArrayList<BoolescheFunktionTree>();
	}

}
