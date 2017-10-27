package model.mindmap.basics;

import java.util.LinkedList;
import java.util.List;

import model.mindmap.evaluation.enums.DifferenceResult;

/**
 * All MindMaps/TOC Word/TOC Latex are internally saved as Trees. This class
 * represents the nodes of these trees and contains all information needed to
 * re-/create output files.
 *
 * @author Magnus Lechner
 *
 */
public class TreeNode {
  
  private int depth = 0;
  
  private double distancePercent = -1.0;
  
  private double maxRating = 0.0;
  private double realRating = 0.0;
  
  private double xOffset = 0.0;
  private double yOffset = 0.0;
  
  private boolean isOptional = false;
  private boolean isSolution = false;
  private boolean isMetaNode = false;
  
  // id is NOT used for identification, it's text instead (yeah i know....)
  // TODO get rid of it, once and for all
  private String id;
  private String text = "";
  private String number = "";
  private TreeNode parent = null;
  private Image image = null;
  
  private DifferenceResult differenceResult = null;
  
  // synonyms MUST contain the text string of the node
  private List<String> synonyms = new LinkedList<>();
  private List<String> editSynonyms = new LinkedList<>();
  private List<String> regexSynonyms = new LinkedList<>();
  private List<String> correctSolutions = new LinkedList<>();
  private List<TreeNode> childNodes = new LinkedList<>();
  
  /**
   * Constructor in case a TreeNode is the root of a tree. The id is currently
   * not needed; it is an artifact and should be removed.
   *
   * @param id
   *          the id of the TreeNode
   */
  public TreeNode(String id) {
    this.id = id;
    this.parent = null;
  }
  
  /**
   * Constructor in case a TreeNode is a inner node or leave of a tree. The id
   * is currently not needed; it is an artifact and should be removed.
   *
   * @param id
   *          the id of the TreeNode
   * @param parent
   *          the parent of the TreeNode
   */
  public TreeNode(String id, TreeNode parent) {
    this.id = id;
    this.parent = parent;
  }
  
  /**
   * Adds a child node to the TreeNode.
   *
   * @param child
   *          the TreeNode which should be a child
   * @return true (as specified by Collection.add(E))
   */
  public boolean addChild(TreeNode child) {
    return childNodes.add(child);
  }
  
  /**
   * Add a solution.
   *
   * @param solution
   */
  public void addCorrectSolution(String solution) {
    correctSolutions.add(solution);
  }
  
  /**
   * Adds a synonym to the synonym list. If this synonym is regex, add it also
   * to the regex synonyms list @see {@link #getRegexSynonyms()}. Else the
   * synonym will also be added to the edit synonyms list
   * {@link #getEditSynonyms()}.
   *
   * @param synonym
   *          to be added
   */
  public void addSynonym(String synonym) {
    if(!synonym.isEmpty()) {
      synonyms.add(synonym);
      calcDifferentSynonymTypes(synonym);
    }
  }
  
  /**
   * Two TreeNodes are equal if their text is the same.
   */
  @Override
  public boolean equals(Object obj) {
    if(this == obj)
      return true;
    if(obj == null)
      return false;
    if(getClass() != obj.getClass())
      return false;
    TreeNode other = (TreeNode) obj;
    if(text == null) {
      if(other.text != null)
        return false;
    } else if(!text.equals(other.text))
      return false;
    return true;
  }
  
  /**
   * Returns all children of the TreeNode as List.
   *
   * @return list with all children of the TreeNode
   */
  public List<TreeNode> getChildren() {
    return childNodes;
  }
  
  /**
   * Returns a list which contains all accepted strings of solutions of a
   * TreeNode.
   *
   * @return solution list
   */
  public List<String> getCorrectSolutions() {
    return correctSolutions;
  }
  
  /**
   * Holds the information on which level in the tree the TreeNode is. NOTE:
   * This method does NOT compute the level of the TreeNode in the tree, it only
   * returns a before set value.
   *
   * @return the depth of the TreeNode in the tree
   */
  public int getDepth() {
    return depth;
  }
  
  /**
   * After evaluation the DifferenceResult shows how the status of this TreeNode
   * is compared to the solution.
   *
   * @return the DifferenceResult
   */
  public DifferenceResult getDifferenceResult() {
    return differenceResult;
  }
  
  /**
   * The percentage of the users input text compared to the correct solution
   * text for this TreeNode. NOTE: the calculation NOT done here.
   *
   * @return
   */
  public double getDistancePercent() {
    return distancePercent;
  }
  
  /**
   * Returns all synonyms from which a edit distance can be calculated.
   *
   * @return edit-synonym list
   */
  public List<String> getEditSynonyms() {
    if(editSynonyms.isEmpty()) {
      editSynonyms.add(text);
    }
    return editSynonyms;
  }
  
  /**
   * The id of the TreeNode. NOTE: id is NOT the identification. It is a
   * artifact
   *
   * @return the id of the TreeNode.
   */
  @Deprecated
  public String getId() {
    return id;
  }
  
  /**
   * Returns the image container which was created by reading a mindmap. The
   * container holds all information necessary to create a image.
   *
   * @return the image container
   */
  public Image getImage() {
    return image;
  }
  
  /**
   * The maximal rating a user can possibly achieve in evaluation for this
   * TreeNode.
   *
   * @return The maximal rating
   */
  public double getMaxRating() {
    return maxRating;
  }
  
  /**
   * If the TreeNode is inside a tree used for TOC, the number represents the
   * numbering value of the TreeNode in its position. As example if the parent
   * has the number 1 than the number of the first child is 1.1 and of the
   * second it is 1.2 and so forward. NOTE: This method does NOT compute the
   * numbering of the TreeNode in the tree, it only returns a before set value.
   *
   * @return the number of the numbering
   */
  public String getNumber() {
    return number;
  }
  
  /**
   * Returns the parent of the TreeNode. Returns null if the TreeNode is the
   * root of the the tree.
   *
   * @return the parent node if it exists
   */
  public TreeNode getParent() {
    return parent;
  }
  
  /**
   * Returns the value a user has achieved. This can differ from maximal rating.
   *
   * @return the real rating
   */
  public double getRealRating() {
    return realRating;
  }
  
  /**
   * Returns all synonyms which are regex.
   *
   * @return regex synonyms
   */
  public List<String> getRegexSynonyms() {
    return regexSynonyms;
  }
  
  /**
   * Returns all synonyms which are equivalent to the text of the TreeNode. This
   * may contain regex.
   *
   * @return list of synonyms
   */
  public List<String> getSynonyms() {
    return synonyms;
  }
  
  /**
   * The text which will be displayed inside the node of a mindmap. NOTE: This
   * is the "id". All TreeNodes must have a unique text.
   *
   * @return the text
   */
  public String getText() {
    return text;
  }
  
  /**
   * Return the x-value-offset. This places the nodes in the mindmaps on the
   * same x coordinate.
   *
   * @return the x-offset
   */
  public double getxOffset() {
    return xOffset;
  }
  
  /**
   * Return the y-value-offset. This places the nodes in the mindmaps on the
   * same y coordinate.
   *
   * @return the y-offset
   */
  public double getyOffset() {
    return yOffset;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((text == null) ? 0 : text.hashCode());
    return result;
  }
  
  /**
   * This is false by default. This should be true if the TreeNode is not needed
   * for evaluation but contains meta data (as example task description).
   *
   * @return true if TreeNode is only meta node
   */
  public boolean isMetaNode() {
    return isMetaNode;
  }
  
  /**
   * This is a flag. If true, the TreeNode is optional. This is important for
   * evaluating. It means that a node is not required for a solution to be
   * correct.
   *
   * @return true if optional
   */
  public boolean isOptional() {
    return isOptional;
  }
  
  /**
   * This is a flag. If true the node is inside a tree which represents the
   * correct solution. Therefore this node must be treated differently for the
   * evaluation part. NOTE: Would be much better if only the root node has this.
   *
   * @return true if node is part of the solution
   */
  public boolean isSolution() {
    return isSolution;
  }
  
  /**
   * Sets the correct solutions.
   *
   * @param correctSolutions
   */
  public void setCorrectSolutions(List<String> correctSolutions) {
    this.correctSolutions = correctSolutions;
  }
  
  /**
   * The beforehand computed level of the TreeNode inside the tree.
   *
   * @param depth
   *          The level inside the tree
   */
  public void setDepth(int depth) {
    this.depth = depth;
  }
  
  /**
   * Sets the DifferenceResult.
   *
   * @param nodeResult
   *          the result
   */
  public void setDifferenceResult(DifferenceResult nodeResult) {
    this.differenceResult = nodeResult;
  }
  
  /**
   * Sets the percentage of the user answer.
   *
   * @param distancePercent
   */
  public void setDistancePercent(double distancePercent) {
    this.distancePercent = distancePercent;
  }
  
  /**
   * The id of the TreeNode. NOTE: id is NOT the identification. It is a
   * artifact
   *
   * @param id
   */
  @Deprecated
  public void setId(String id) {
    this.id = id;
  }
  
  /**
   * Makes the TreeNode hold the image.
   *
   * @param image
   *          the image of the TreeNode
   */
  public void setImage(Image image) {
    this.image = image;
  }
  
  /**
   * Sets the maximal rating.
   *
   * @param maxRating
   *          The rating
   */
  public void setMaxRating(double maxRating) {
    this.maxRating = maxRating;
  }
  
  /**
   * Sets flag if TreeNode is meta node.
   *
   * @param isMetaNode
   *          true if meta node
   */
  public void setMetaNode(boolean isMetaNode) {
    this.isMetaNode = isMetaNode;
  }
  
  /**
   * Set the numbering for a TreeNode used for a TOC tree.
   *
   * @param number
   *          the numbering of the specific node
   */
  public void setNumber(String number) {
    this.number = number;
  }
  
  /**
   * Sets the flag if a TreeNode is optional or not.
   *
   * @param isOptional
   *          true if TreeNode must be optional
   */
  public void setOptional(boolean isOptional) {
    this.isOptional = isOptional;
  }
  
  /**
   * Sets a TreeNode as parent of this node.
   *
   * @param parent
   *          the parent of the node.
   */
  public void setParent(TreeNode parent) {
    this.parent = parent;
  }
  
  /**
   * Sets the real rating.
   *
   * @param realRating
   */
  public void setRealRating(double realRating) {
    this.realRating = realRating;
  }
  
  /**
   * Sets the flag if a TreeNode is part of the solution or not.
   *
   * @param isSolution
   *          true if TreeNode must be part of the solution
   */
  public void setSolution(boolean isSolution) {
    this.isSolution = isSolution;
  }
  
  /**
   * Set synonyms of this TreeNode. The text of the TreeNode is always added to
   * the synonyms list. Also calculate out of these synonyms all synonyms which
   * are regex and which are not regex @see
   * {@link #calcDifferentSynonymTypes()}.
   *
   * @param synonyms
   */
  public void setSynonyms(List<String> synonyms) {
    if(text.isEmpty()) {
      this.synonyms = synonyms;
    } else {
      if(!synonyms.contains(text)) {
        synonyms.add(text);
      }
      this.synonyms = synonyms;
    }
    calcDifferentSynonymTypes();
  }
  
  /**
   * The text which will be displayed inside the node of a mindmap. NOTE: This
   * is the "id". All TreeNodes must have a unique text.
   *
   * @param text
   *          the text of the node
   */
  public void setText(String text) {
    this.text = text;
  }
  
  /**
   * Sets the x-offset value.
   *
   * @param xOffset
   */
  public void setxOffset(double xOffset) {
    this.xOffset = xOffset;
  }
  
  /**
   * Sets the y-offset value.
   *
   * @param yOffset
   */
  public void setyOffset(double yOffset) {
    this.yOffset = yOffset;
  }
  
  /**
   * All synonyms which contains regex are added to a regex-list. All other
   * synonyms get added to a edit-list. This is to calculate the edit distance
   * later on if possible.
   */
  private void calcDifferentSynonymTypes() {
    editSynonyms = new LinkedList<>();
    regexSynonyms = new LinkedList<>();
    for(String synonym: synonyms) {
      if(!synonym.isEmpty()) {
        if(synonym.matches("[\\w\\s]+")) {
          editSynonyms.add(synonym);
        } else {
          regexSynonyms.add(synonym);
        }
      }
    }
  }
  
  /**
   * If the synonym contains regex, it is added to the regex-list. Else it is
   * added to the edit-list.
   */
  private void calcDifferentSynonymTypes(String synonym) {
    if(!synonym.isEmpty()) {
      if(synonym.matches("\\w+")) {
        editSynonyms.add(synonym);
      } else {
        regexSynonyms.add(synonym);
      }
    }
  }
  
}
