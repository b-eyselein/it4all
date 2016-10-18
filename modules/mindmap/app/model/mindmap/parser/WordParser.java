package model.mindmap.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import model.mindmap.basics.TreeNode;
import model.mindmap.parser.basics.Level;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.apache.xmlbeans.XmlException;

public class WordParser extends AbstractParser {

  private TreeNode currentNode = new TreeNode("0");
  private int id = 1;
  private int currentDepth = 0;

  @Override
  public List<TreeNode> read(File file) throws InvalidFormatException, IOException {
    LinkedList<TreeNode> roots = new LinkedList<>();
    roots.add(currentNode);
    XWPFDocument xdoc = new XWPFDocument(OPCPackage.open(file));
    List<XWPFParagraph> paragraphList = xdoc.getParagraphs();
    for(XWPFParagraph paragraph: paragraphList) {
      int depth = Level.getDepth(paragraph.getStyle());
      if(depth >= 0) {
        if(!paragraph.getText().isEmpty()) {
          buildTree(depth, paragraph.getText());
        }
      }
    }
    numerateAllTrees(roots);
    xdoc.close();
    return roots;
  }

  @Override
  public void write(String filepath, List<TreeNode> rootList, String templatePath) throws IOException, XmlException {
    TreeNode root = rootList.get(0);
    XWPFDocument template = new XWPFDocument(new FileInputStream(new File(templatePath)));
    XWPFDocument xdoc = new XWPFDocument();
    XWPFStyles newStyles = xdoc.createStyles();
    // don't know how to set numbering in new .docx from template
    // XWPFNumbering numbering = xdoc.createNumbering();
    // template.getNumbering();
    newStyles.setStyles(template.getStyle());
    createContent(xdoc, root, 0);
    xdoc.write(new FileOutputStream(new File(filepath)));
    template.close();
  }

  private void addStyledParagraphOfText(XWPFDocument xdoc, String styleDef, String text) {
    if(text.isEmpty())
      return;
    XWPFParagraph paragraph = xdoc.createParagraph();
    paragraph.setStyle(styleDef);
    XWPFRun tmpRun = paragraph.createRun();
    tmpRun.setText(text);
  }

  private void buildTree(int depth, String text) {
    if(depth == 0) {
      while(currentNode.getParent() != null) {
        currentNode = currentNode.getParent();
      }
      currentNode.setText(text);
      return;
    }
    if(depth > currentDepth) {
      for(int i = 0; i < depth - currentDepth; i++) {
        TreeNode treeNode = new TreeNode(String.valueOf(id));
        treeNode.setParent(currentNode);
        id++;
        currentNode.addChild(treeNode);
        currentNode = treeNode;
        if(depth - currentDepth == i + 1) {
          currentNode.setText(text);
        }
      }
    }
    if(depth == currentDepth) {
      currentNode = currentNode.getParent();
      TreeNode treeNode = new TreeNode(String.valueOf(id));
      treeNode.setParent(currentNode);
      id++;
      currentNode.addChild(treeNode);
      currentNode = treeNode;
      currentNode.setText(text);
    }
    if(depth < currentDepth) {
      for(int i = 0; i < (currentDepth - depth) + 1; i++) {
        currentNode = currentNode.getParent();
      }
      TreeNode treeNode = new TreeNode(String.valueOf(id));
      treeNode.setParent(currentNode);
      id++;
      currentNode.addChild(treeNode);
      currentNode = treeNode;
      currentNode.setText(text);
    }
    currentDepth = depth;
  }

  private void createContent(XWPFDocument xdoc, TreeNode treeNode, int depth) {
    addStyledParagraphOfText(xdoc, Level.getLevelGerman(depth), treeNode.getText());
    for(TreeNode n: treeNode.getChildren()) {
      createContent(xdoc, n, depth + 1);
    }
  }
}
