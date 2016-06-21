package model.boolescheAlgebra.BFTree;

import java.util.List;

public class BF_XOR implements BFKnoten{
	
	private BFKnoten linkerK;
	private BFKnoten rechterK;
	
	public BF_XOR(BFKnoten l, BFKnoten r) {
		this.linkerK = l;
		this.rechterK = r;
	}
	
	@Override
	public boolean getWert() {
		return (this.linkerK.getWert() ^ this.rechterK.getWert());
	}
	
	@Override
	public String toString() {
		return this.linkerK.toString()+" XOR "+this.rechterK.toString();
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
