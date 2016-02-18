package model.mindmap.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.mindmap.basics.TreeNode;

public class LatexParser extends AbstractParser {

	//regex: [^}] = all characters except }  and *? reluctant quantifier searches for zero or more times
	//has to be done this way or else there would be ordering issues
    private static final Pattern PATTERN = Pattern.compile("\\\\title\\{([^}]*?)\\}|"
    		+ "\\\\chapter\\{([^}]*?)\\}|\\\\section\\{([^}]*?)\\}|"
    		+ "\\\\subsection\\{([^}]*?)\\}|\\\\subsubsection\\{([^}]*?)\\}|"
    		+ "\\\\paragraph\\{([^}]*?)\\}|\\\\subparagraph\\{([^}]*?)\\}");
    
    private int id = 0;
    
    @Override
	public LinkedList<TreeNode> read(File file) throws IOException {
		int prevDepth = 1;
		TreeNode root = new TreeNode(Integer.toString(id));
		id++;
		TreeNode currentNode = root;
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
	    for(String line; (line = br.readLine()) != null; ) {
	        if(line.startsWith("%")) {
	        	continue;
	        }
	        Matcher m = PATTERN.matcher(line);
	        while(m.find()) {
	        	if(m.group(1) != null) {
	        		root.setText(m.group(1));
	        		currentNode = root;
	        		root.setDepth(1);
	        		prevDepth = 1;
	        	}else if(m.group(2) != null) {
	        		currentNode = buildTree(2, prevDepth, currentNode, m.group(2));
	        		prevDepth = 2;
	        	}else if(m.group(3) != null) {
	        		currentNode = buildTree(3, prevDepth, currentNode, m.group(3));
	        		prevDepth = 3;
	        	}else if(m.group(4) != null) {
	        		currentNode = buildTree(4, prevDepth, currentNode, m.group(4));
	        		prevDepth = 4;
	        	}else if(m.group(5) != null) {
	        		currentNode = buildTree(5, prevDepth, currentNode, m.group(5));
	        		prevDepth = 5;
	        	}else if(m.group(6) != null) {
	        		currentNode = buildTree(6, prevDepth, currentNode, m.group(6));
	        		prevDepth = 6;
	        	}else if(m.group(7) != null) {
	        		currentNode = buildTree(7, prevDepth, currentNode, m.group(7));
	        		prevDepth = 7;
	        	}
	        }
	    }
	    br.close();
		numerateOneTree(root, "");
		LinkedList<TreeNode> res = new LinkedList<TreeNode>();
		res.add(root);
		return res;
	}
	
	private TreeNode buildTree(int depth, int prevDepth, TreeNode currentNode, String text) {
		if(depth <= prevDepth) {
			TreeNode parent;
			for(int i = 0; i < prevDepth+1-depth; i++) {
				parent = currentNode.getParent();
				currentNode = parent;
			}
		}
		TreeNode child = new TreeNode(Integer.toString(id), currentNode);
		child.setText(text);
		currentNode.addChild(child);
		id++;
		currentNode = child;
		return currentNode;
	}
	
	@Override
	public void write(String filepath, LinkedList<TreeNode> listOfRoots, String preamblePath) throws IOException {
		//for this kind of application only taking one root makes sense
		TreeNode treeNode = listOfRoots.get(0);
		String title;
		if(treeNode.getText().equals("")) {
			title = "\\title{Insert title here}\n";
		}else{
			title = "\\title{" + treeNode.getText() + "}\n";
		}		
		StringBuilder preamble = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader(new File(preamblePath)));
	    for(String line; (line = br.readLine()) != null; ) {
	    	preamble.append(line);
	    	preamble.append("\n");
	    }
	    br.close();
		String beginDocument = "\n\\begin{document}\n\n"
				+ title
				+ "\\author{Max Musterman}\n"
				+ "\\maketitle\n"
				+ "\\tableofcontents\n"
				+ "\n";
		String endDocument = "\\end{document}\n"; 
		StringBuilder sb = new StringBuilder();
		sb.append(preamble);
		sb.append(beginDocument);
		traverseTree(treeNode, sb, 1);
		sb.append(endDocument);
		File file = new File(filepath);
		Writer writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
	    writer.append(sb);
	    writer.close();
	}
	
	private void traverseTree(TreeNode node, StringBuilder sb, int depth) {
		switch(depth) {
		case 2:
			sb.append("\\chapter{" + node.getText() + "}\n\n");
			break;
		case 3:
			sb.append("\\section{" + node.getText() + "}\n\n");
			break;
		case 4:
			sb.append("\\subsection{" + node.getText() + "}\n\n");
			break;
		case 5:
			sb.append("\\subsubsection{" + node.getText() + "}\n\n");
			break;
		case 6:
			sb.append("\\paragraph{" + node.getText() + "}\n\n");
			break;
		}
		if(depth >= 7) {
			sb.append("\\subparagraph{" + node.getText() + "}\n\n");
		}
		for(TreeNode n : node.getChildren()) {
			traverseTree(n, sb, depth+1);
		}
	}
	
}
