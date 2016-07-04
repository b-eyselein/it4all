package model.boolescheAlgebra.BFTree;

import java.util.List;

public class BF_XOR implements BFKnoten {
  
  private BFKnoten linkerK;
  private BFKnoten rechterK;
  
  public BF_XOR(BFKnoten l, BFKnoten r) {
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
    return (this.linkerK.getWert() ^ this.rechterK.getWert());
  }
  
  @Override
  public String toString() {
    String s;
    if(this.linkerK.getClass() == BF_XOR.class || this.linkerK.getClass() == BF_Variable.class
        || this.linkerK.getClass() == BF_NOT.class || this.linkerK.getClass() == BF_1.class
        || this.linkerK.getClass() == BF_0.class) {
      s = "" + linkerK.toString() + " XOR ";
    } else {
      s = "(" + linkerK.toString() + ") XOR ";
    }
    if(this.rechterK.getClass() == BF_XOR.class || this.rechterK.getClass() == BF_Variable.class
        || this.rechterK.getClass() == BF_NOT.class || this.rechterK.getClass() == BF_1.class
        || this.rechterK.getClass() == BF_0.class) {
      s += rechterK.toString();
    } else {
      s += "(" + rechterK.toString() + ")";
    }
    return s;
  }
  
}
