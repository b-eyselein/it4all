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
	  String s;
	  if (this.linkerK.getClass() == BF_OR.class || this.linkerK.getClass() == BF_XOR.class) {
	    s = "("+linkerK.toString()+") AND ";
	  } else {
	    s = ""+linkerK.toString()+" AND ";
    }
	  if (this.rechterK.getClass() == BF_OR.class || this.rechterK.getClass() == BF_XOR.class) {
      s += "("+rechterK.toString()+")";
    } else {
      s += rechterK.toString();
    }
		return s;
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
