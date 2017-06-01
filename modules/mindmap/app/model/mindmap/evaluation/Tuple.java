package model.mindmap.evaluation;

import model.mindmap.basics.TreeNode;

public class Tuple {

  private TreeNode firstNode;
  private TreeNode secondNode;

  public Tuple(TreeNode firstNode, TreeNode secondNode) {
    this.firstNode = firstNode;
    this.secondNode = secondNode;
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj)
      return true;
    if(obj == null)
      return false;
    if(getClass() != obj.getClass())
      return false;
    Tuple other = (Tuple) obj;
    if(firstNode == null) {
      if(other.firstNode != null)
        return false;
    } else if(!firstNode.equals(other.firstNode))
      return false;
    if(secondNode == null) {
      if(other.secondNode != null)
        return false;
    } else if(!secondNode.equals(other.secondNode))
      return false;
    return true;
  }

  public TreeNode getFirstNode() {
    return firstNode;
  }

  public TreeNode getSecondNode() {
    return secondNode;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((firstNode == null) ? 0 : firstNode.hashCode());
    result = prime * result + ((secondNode == null) ? 0 : secondNode.hashCode());
    return result;
  }

}
