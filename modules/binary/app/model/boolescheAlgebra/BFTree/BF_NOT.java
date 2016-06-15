package model.boolescheAlgebra.BFTree;

import java.util.List;

public class BF_NOT implements BFKnoten {
	
	private BFKnoten knoten;
	
	public BF_NOT(BFKnoten k) {
		this.knoten = k;
	}
	
	@Override
	public boolean getWert() {
		return !(this.knoten.getWert());
	}
	
	@Override
	public String toString() {
		return "NOT("+this.knoten.toString()+")";
	}

	@Override
	public List<BoolescheFunktionTree> getTeilformeln(BF_Variable[] vars) {
		//System.out.println(knoten.getClass()); // TODO: remove
		List<BoolescheFunktionTree> list = this.knoten.getTeilformeln(vars);
		list.add(new BoolescheFunktionTree(this, vars));
		return list;
	}

}