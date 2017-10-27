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
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.mindmap.basics.TreeNode;
import model.mindmap.evaluation.ParsingException;

public class LatexParser implements AbstractParser {
  
  // regex: [^}] = all characters except } and *? reluctant quantifier searches
  // for zero or more times
  // has to be done this way or else there would be ordering issues
  private static final Pattern PATTERN = Pattern
      .compile("\\\\title\\{([^}]*?)\\}|" + "\\\\chapter\\{([^}]*?)\\}|\\\\section\\{([^}]*?)\\}|"
          + "\\\\subsection\\{([^}]*?)\\}|\\\\subsubsection\\{([^}]*?)\\}|"
          + "\\\\paragraph\\{([^}]*?)\\}|\\\\subparagraph\\{([^}]*?)\\}");
  
  private int id = 0;
  
  @Override
  public List<TreeNode> read(File file) throws ParsingException {
    try {
      int prevDepth = 1;
      TreeNode root = new TreeNode(Integer.toString(id));
      id++;
      TreeNode currentNode = root;
      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
      for(String line; (line = br.readLine()) != null;) {
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
          } else if(m.group(2) != null) {
            currentNode = buildTree(2, prevDepth, currentNode, m.group(2));
            prevDepth = 2;
          } else if(m.group(3) != null) {
            currentNode = buildTree(3, prevDepth, currentNode, m.group(3));
            prevDepth = 3;
          } else if(m.group(4) != null) {
            currentNode = buildTree(4, prevDepth, currentNode, m.group(4));
            prevDepth = 4;
          } else if(m.group(5) != null) {
            currentNode = buildTree(5, prevDepth, currentNode, m.group(5));
            prevDepth = 5;
          } else if(m.group(6) != null) {
            currentNode = buildTree(6, prevDepth, currentNode, m.group(6));
            prevDepth = 6;
          } else if(m.group(7) != null) {
            currentNode = buildTree(7, prevDepth, currentNode, m.group(7));
            prevDepth = 7;
          }
        }
      }
      br.close();
      numerateOneTree(root, "");
      return Arrays.asList(root);
    } catch (Exception e) {
      throw new ParsingException(e);
    }
  }
  
  @Override
  public void write(Path filepath, List<TreeNode> listOfRoots, Path preamblePath) throws ParsingException {
    try(FileReader fileReader = new FileReader(preamblePath.toFile());
        FileOutputStream fileOutputStream = new FileOutputStream(filepath.toFile())) {
      // for this kind of application only taking one root makes sense
      TreeNode treeNode = listOfRoots.get(0);
      String title;
      if(treeNode.getText().isEmpty()) {
        title = "\\title{Insert title here}\n";
      } else {
        title = "\\title{" + treeNode.getText() + "}\n";
      }
      StringBuilder preamble = new StringBuilder();
      BufferedReader br = new BufferedReader(fileReader);
      for(String line; (line = br.readLine()) != null;) {
        preamble.append(line);
        preamble.append("\n");
      }
      br.close();
      String beginDocument = "\n\\begin{document}\n\n" + title + "\\author{Max Musterman}\n" + "\\maketitle\n"
          + "\\tableofcontents\n" + "\n";
      String endDocument = "\\end{document}\n";
      StringBuilder sb = new StringBuilder();
      sb.append(preamble);
      sb.append(beginDocument);
      traverseTree(treeNode, sb, 1);
      sb.append(endDocument);

      Writer writer = new OutputStreamWriter(fileOutputStream, "UTF-8");
      writer.append(sb);
      writer.close();
    } catch (IOException e) {
      throw new ParsingException(e);
    }
  }
  
  private TreeNode buildTree(int depth, int prevDepth, TreeNode currentNode, String text) {
    if(depth <= prevDepth) {
      TreeNode parent;
      for(int i = 0; i < prevDepth + 1 - depth; i++) {
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
  
  private void traverseTree(TreeNode node, StringBuilder sb, int depth) {
    String chapterType;
    switch(depth) {
    case 2:
      chapterType = "chapter";
      break;
    case 3:
      chapterType = "section";
      break;
    case 4:
      chapterType = "subsection";
      break;
    case 5:
      chapterType = "subsubsection";
      break;
    case 6:
      chapterType = "paragraph";
      break;
    default:
      chapterType = "subparagraph";
      break;
    }
    sb.append("\\" + chapterType + "{" + node.getText() + "}\n\n");
    for(TreeNode n: node.getChildren()) {
      traverseTree(n, sb, depth + 1);
    }
  }
  
}
