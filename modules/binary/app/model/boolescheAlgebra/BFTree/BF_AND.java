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
  public List<BoolescheFunktionTree> getTeilformeln(BF_Variable[] vars) {
    List<BoolescheFunktionTree> llist = this.linkerK.getTeilformeln(vars);
    List<BoolescheFunktionTree> rlist = this.rechterK.getTeilformeln(vars);
    llist.addAll(rlist);
    llist.add(new BoolescheFunktionTree(this, vars));
    return llist;
  }
  
  @Override
  public boolean getWert() {
    return (linkerK.getWert() && rechterK.getWert());
  }
  
  @Override
  public String toString() {
    String s;
    if(this.linkerK.getClass() == BF_AND.class || this.linkerK.getClass() == BF_Variable.class
        || this.linkerK.getClass() == BF_NOT.class || this.linkerK.getClass() == BF_1.class
        || this.linkerK.getClass() == BF_0.class) {
      s = "" + linkerK.toString() + " AND ";
    } else {
      s = "(" + linkerK.toString() + ") AND ";
    }
    if(this.rechterK.getClass() == BF_AND.class || this.rechterK.getClass() == BF_Variable.class
        || this.rechterK.getClass() == BF_NOT.class || this.rechterK.getClass() == BF_1.class
        || this.rechterK.getClass() == BF_0.class) {
      s += rechterK.toString();
    } else {
      s += "(" + rechterK.toString() + ")";
    }
    return s;
  }
  
}
