package model.boolescheAlgebra.BFTree;

import java.util.List;

public class BF_AND implements BFKnoten {
	
	private BFKnoten linkerK;
	private BFKnoten rechterK;
	
	public BF_AND(BFKnoten l, BFKnoten r) {
		
		this.linkerK = l;
		this.rechterK = r;
	}
	
	@Override
	public boolean getWert() {
		return (linkerK.getWert() && rechterK.getWert());
	}
	
	@Override
	public String toString() {
		return "("+linkerK.toString()+" AND "+rechterK.toString()+")";
	}
	
	@Override
	public List<BoolescheFunktionTree> getTeilformeln(BF_Variable[] vars) {
		List<BoolescheFunktionTree> llist = this.linkerK.getTeilformeln(vars);
		List<BoolescheFunktionTree> rlist = this.rechterK.getTeilformeln(vars);
		llist.addAll(rlist);
		llist.add(new BoolescheFunktionTree(this, vars));
		return llist;
	}

}
