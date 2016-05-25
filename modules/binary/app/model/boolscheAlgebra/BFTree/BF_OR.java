package model.boolscheAlgebra.BFTree;

import java.util.List;

public class BF_OR implements BFKnoten {
	
	private BFKnoten linkerK;
	private BFKnoten rechterK;
	
	public BF_OR(BFKnoten l, BFKnoten r) {
		this.linkerK = l;
		this.rechterK = r;
	}
	
	@Override
	public boolean getWert() {
		return (this.linkerK.getWert() || this.rechterK.getWert());
	}
	
	@Override
	public String toString() {
		return "("+this.linkerK.toString()+" OR "+this.rechterK.toString()+")";
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
