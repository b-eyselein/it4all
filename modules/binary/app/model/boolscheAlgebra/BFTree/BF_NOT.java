package model.boolscheAlgebra.BFTree;

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
	public List<BoolscheFormelTree> getTeilformeln(BF_Variable[] vars) {
		//System.out.println(knoten.getClass()); // TODO: remove
		List<BoolscheFormelTree> list = this.knoten.getTeilformeln(vars);
		list.add(new BoolscheFormelTree(this, vars));
		return list;
	}

}
