package model.mindmap.parser;

import java.io.File;
import java.util.LinkedList;

import model.mindmap.basics.TreeNode;

public abstract class AbstractParser {

	/**
	 * This method reads a mindmap in xml or xmmap format and creates an internal
	 * representation of the mindmap as tree structure. As consequence, if there
	 * are more than one root node, a list of trees will be generated.
	 * 
	 * @param file file path to mindmap
	 * @return List of TreeNodes which are the roots of their tree. 
	 * @throws Exception
	 */
	public abstract LinkedList<TreeNode> read(File file) throws Exception;
	
	/**
	 * This method creates from the internal representation (trees) a mindmap.
	 * The creation is recursive for each trees.
	 * 
	 * @param filepath file path where the mindmap will be created
	 * @param rootList a list of TreeNodes. Each of this nodes is the root of
	 * 		  its tree. 
	 * @param templatePath file path to the template which will be used. This
	 * 		  changes only the appearance of the mindmap
	 * @throws Exception
	 */
	public abstract void write(String filepath, LinkedList<TreeNode> rootList, String templatePath) throws Exception;

	/**
	 * prints the text of all nodes for all trees
	 * 
	 * @param listOfRoots root nodes of the trees
	 */
	public void printAllTrees(LinkedList<TreeNode> listOfRoots) {
		for(TreeNode n : listOfRoots) {
			System.out.println("NEW TREE:");
			printOneTree(n);
			System.out.println("TREE END");
			System.out.println("");
		}
	}
	
	/**
	 * prints all nodes in the specific tree.
	 * 
	 * @param node root of the tree
	 */
	public void printOneTree(TreeNode node) {
		if(node.getNumber().equals("")) {
			System.out.println(node.getText());
		}else{
			System.out.println(node.getNumber() + " " + node.getText());	
		}
		for(TreeNode n : node.getChildren()) {
			printOneTree(n);
		}
	}
	
	/**
	 * numbering of nodes in table of content style for all trees
	 * 
	 * @param listOfRoots roots of trees
	 */
	public void numerateAllTrees(LinkedList<TreeNode> listOfRoots) {
		for(TreeNode n : listOfRoots) {
			numerateOneTree(n, "");
		}
	}
	
	/**
	 * numbering of nodes in table of content style for one tree
	 * 
	 * @param node root node
	 * @param number current numbering of a node
	 */
	public void numerateOneTree(TreeNode node, String number) {
		int k = 1; 
		for(TreeNode n : node.getChildren()) {
			String numeration = number;
			if(number.length() == 0) {
				numeration = Integer.toString(k);
			}else{
				numeration += "." + Integer.toString(k);
			}
			k++;
			n.setNumber(numeration);
			numerateOneTree(n, numeration);
		}
	}
}
