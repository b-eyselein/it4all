package model.boolscheAlgebra.BFTree;

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
	public List<BoolscheFormelTree> getTeilformeln(BF_Variable[] vars) {
		List<BoolscheFormelTree> llist = this.linkerK.getTeilformeln(vars);
		List<BoolscheFormelTree> rlist = this.rechterK.getTeilformeln(vars);
		llist.addAll(rlist);
		llist.add(new BoolscheFormelTree(this, vars));
		return llist;
	}

}
