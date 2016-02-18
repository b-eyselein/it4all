package model.mindmap.parser;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import model.mindmap.basics.TreeNode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class FreePlanParser extends AbstractEvaluationParser {
	
	@Override
	public LinkedList<TreeNode> read(File file) throws ParserConfigurationException, SAXException, IOException {
		LinkedList<TreeNode> listOfRoots = new LinkedList<TreeNode>();
        DocumentBuilderFactory dbFactory 
           = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();	 
        listOfRoots = buildTree(doc);
		return listOfRoots;
	}
	
	private LinkedList<TreeNode> buildTree(Document doc) {
		//root element from DOM - NOT the root of the tree
		LinkedList<TreeNode> listOfRoots = new LinkedList<TreeNode>();
		Element rootElement = doc.getDocumentElement();
        NodeList nodes = rootElement.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
        	Node node = nodes.item(i);
        	if(node.getNodeType() == Node.ELEMENT_NODE){
        		Element element = (Element) node;
        		TreeNode root = 							
        				new TreeNode(element.getAttribute("ID"));
        		if(element.getAttribute("TEXT").equals("")) {
        			root.setText("Insert title here");
        		}else{
        			root.setText(element.getAttribute("TEXT"));	
        		}
        		listOfRoots.add(root);
        		buildTreeRecursive(node, root, "", listOfRoots, true);
        	}
        }
        return listOfRoots;
	}
	
	//need flag firstLevel to decide if a free node is a free root node or a child of a free root node
	private void buildTreeRecursive(Node nodeDOM, TreeNode treeNode, String number,
			LinkedList<TreeNode> listOfRoots, boolean firstLevel) {
		Element el = (Element) nodeDOM;
		NodeList childNodesDOM = el.getChildNodes();
		int k = 1; 
		for(int i = 0; i < childNodesDOM.getLength(); i++) {
			Node node = childNodesDOM.item(i);
        	if(node.getNodeType() == Node.ELEMENT_NODE){
        		Element elementChild = (Element) node;
        		if(elementChild.getTagName().equals("node")) {
        			if(isFreeNode(elementChild) && firstLevel) {
            			TreeNode newRoot = 										
                				new TreeNode(elementChild.getAttribute("ID"));	
            			newRoot.setText(elementChild.getAttribute("TEXT"));
            			listOfRoots.add(newRoot);
            			buildTreeRecursive(node, newRoot, "", listOfRoots, false);
        			}else{
            			String numeration = number;
            			if(number.length() == 0) {
            				numeration = Integer.toString(k);
            			}else{
            				numeration += "." + Integer.toString(k);
            			}
            			k++;
            			TreeNode newTreeNode = 										
                				new TreeNode(elementChild.getAttribute("ID"), treeNode);	
            			newTreeNode.setText(elementChild.getAttribute("TEXT"));
            			newTreeNode.setNumber(numeration);
            			treeNode.addChild(newTreeNode);
            			buildTreeRecursive(node, newTreeNode, numeration, listOfRoots, false);	
        			}
        		}
        	}
		}
	}
	
	private boolean isFreeNode(Element element) {
		NodeList childNodes = element.getChildNodes();
		for(int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE){
        		Element elementChild = (Element) node;
        		if(elementChild.getTagName().equals("hook")) {
        			if(elementChild.getAttribute("NAME").equals("FreeNode")) {
        				return true;
        			}
        		}
			}
		}
		return false;
	}
	
	//Note: templatePath not supported
	@Override
	public void write(String filepath, LinkedList<TreeNode> listOfRoots, String templatePath) throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		// root element
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("map");
		doc.appendChild(rootElement);
		rootElement.setAttribute("version", "freeplane 1.3.0");	
		//write main tree
		TreeNode treeNode = listOfRoots.get(0);
		Element mainTreeRootElement = writeMainTree(treeNode, rootElement, doc);	
		//write free trees
		for(int i = 1; i < listOfRoots.size(); i++) {
			writeFreeTree(listOfRoots.get(i), mainTreeRootElement, doc);	
		}
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		StreamResult result = new StreamResult(new File(filepath));
		transformer.setOutputProperty(OutputKeys.ENCODING, "us-ascii");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(doc);
		transformer.transform(source, result);
	}
	
	@SuppressWarnings("deprecation")
	private Element writeMainTree(TreeNode treeNode, Element element, Document doc) {
		Element newElement = doc.createElement("node");
		element.appendChild(newElement);
		if(treeNode.getNumber().length() == 0) {
			newElement.setAttribute("TEXT", treeNode.getText());	
		}else{
			newElement.setAttribute("TEXT", treeNode.getNumber() + " " + treeNode.getText());	
		}
		newElement.setAttribute("POSITION", "right");
		newElement.setAttribute("ID", treeNode.getId());		
		for(TreeNode tn : treeNode.getChildren()) {
			writeMainTree(tn, newElement, doc);
		}
		return newElement;
	}
	
	@SuppressWarnings("deprecation")
	private void writeFreeTree(TreeNode treeNode, Element element, Document doc) {
		//create <node> element
		Element newNodeElement = doc.createElement("node");
		element.appendChild(newNodeElement);
		newNodeElement.setAttribute("POSITION", "right");
		newNodeElement.setAttribute("ID", treeNode.getId());
		//create <hook> element
		Element hookElement = doc.createElement("hook");
		newNodeElement.appendChild(hookElement);
		hookElement.setAttribute("NAME", "FreeNode");
		if(treeNode.getNumber().length() == 0) {
			newNodeElement.setAttribute("TEXT", treeNode.getText());
			//create <edge> element with invisible edges to real root node
			Element edgeElement = doc.createElement("edge");
			newNodeElement.appendChild(edgeElement);
			edgeElement.setAttribute("STYLE", "hide_edge");
		}else{
			newNodeElement.setAttribute("TEXT", treeNode.getNumber() + " " + treeNode.getText());
			//create <edge> element with linear edges
			Element edgeElement = doc.createElement("edge");
			newNodeElement.appendChild(edgeElement);
			edgeElement.setAttribute("STYLE", "linear");
		}
		for(TreeNode tn : treeNode.getChildren()) {
			writeFreeTree(tn, newNodeElement, doc);
		}
	}
}
