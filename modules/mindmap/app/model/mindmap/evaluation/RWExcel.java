package model.mindmap.evaluation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import model.mindmap.basics.TreeNode;
import model.mindmap.evaluation.enums.MetaDataState;
import model.mindmap.evaluation.enums.Modus;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class RWExcel {

  private int row;
  private int col;
  private int maxDepthTrees;
  private Util util = new Util();

  /**
   * Creates an empty meta file with the hierarchical structure of the trees and
   * place for synonyms and so on.
   *
   * @param metaFilePath
   *          path to meta file
   * @param solutionRoots
   *          roots of solution trees
   * @throws IOException
   */
  public void createEmptyMetaFile(String metaFilePath, List<TreeNode> solutionRoots) throws IOException {
    HSSFWorkbook workbook = new HSSFWorkbook();
    Sheet excelSheet = workbook.createSheet("Meta Daten");
    createEmptyMetaContent(excelSheet, solutionRoots);
    FileOutputStream fileOut = new FileOutputStream(metaFilePath);
    workbook.write(fileOut);
    fileOut.close();
    workbook.close();
  }

  /**
   * Creates a meta file which takes the state of TreeNodes into account.
   *
   * @param metaFilePath
   *          path where the meta file will be created
   * @param solutionRoots
   *          roots of the solution trees
   * @throws IOException
   */
  public void createMetaFile(String metaFilePath, List<TreeNode> solutionRoots) throws IOException {
    Workbook workbook = new HSSFWorkbook();
    Sheet excelSheet = workbook.createSheet("Meta Daten");
    createMetaContent(excelSheet, solutionRoots);
    FileOutputStream fileOut = new FileOutputStream(metaFilePath);
    workbook.write(fileOut);
    fileOut.close();
    workbook.close();
  }

  /**
   * This method does either create an empty meta file based on a solution
   * mindmap, modify the state of the TreeNodes which represent the internal
   * representation of the mindmap or creates a meta file depending on the given
   * mindmap.
   *
   * @param metaFilePath
   *          path to meta file
   * @param solutionRoots
   *          roots of the solution trees
   * @param state
   *          decides how the meta file is created or takes influence
   * @throws IOException
   */
  public void handleMetaData(String metaFilePath, List<TreeNode> solutionRoots, MetaDataState state)
      throws IOException {
    if(state == MetaDataState.EMPTY) {
      createEmptyMetaFile(metaFilePath, solutionRoots);
    } else if(state == MetaDataState.FROM_METAFILE) {
      readMetaFile(metaFilePath, solutionRoots);
    } else if(state == MetaDataState.FROM_MINDMAP) {
      createMetaFile(metaFilePath, solutionRoots);
    }
  }

  /**
   * reads a meta file and changes the state of TreeNodes depending on the
   * content of the file. This may influence as example if the node is optional
   * or not and what are correct synonyms for a node. Also some properties which
   * are declared inside the meta file are returned.
   *
   * @param metaFilePath
   *          path of the meta file
   * @param solutionRoots
   *          roots of the trees
   * @return properties which are necessary for the parser
   * @throws IOException
   */
  public Properties readMetaFile(String metaFilePath, List<TreeNode> solutionRoots) throws IOException {
    resetCounter();
    Properties properties = new Properties();
    FileInputStream fileInStream = new FileInputStream(new File(metaFilePath));
    HSSFWorkbook workbook = new HSSFWorkbook(fileInStream);
    HSSFSheet sheet = workbook.getSheetAt(0);

    int synonymColumnNum = -1;
    boolean firstRow = true;
    for(Iterator<Row> rowIterator = sheet.iterator(); rowIterator.hasNext();) {
      Row currentRow = rowIterator.next();
      Iterator<Cell> cellIterator = currentRow.cellIterator();
      String key = null;
      TreeNode currentNode = null;
      LinkedList<String> synonymList = new LinkedList<>();
      // first row allows to check in which columns the meta data are
      // this is necessary if not all data have a value by default
      if(firstRow) {
        // -2 because of zero counting start and last row is modus
        synonymColumnNum = currentRow.getLastCellNum() - 2;
        firstRow = false;
      } else {
        if(cellIterator.hasNext()) {
          // this gives me the text and therefore access to the node in the tree
          Cell cell = cellIterator.next();
          key = cell.getStringCellValue();
          currentNode = getCurrentTreeNode(key, solutionRoots);
          // show in the specific cells if there is any input
          Cell optionalCell = currentRow.getCell(synonymColumnNum - 2);
          if(optionalCell != null && optionalCell.getCellType() != Cell.CELL_TYPE_BLANK) {
            if("YES".equalsIgnoreCase(optionalCell.getStringCellValue())) {
              currentNode.setOptional(true);
            }
          } else {
            currentNode.setOptional(false);
          }
          Cell ratingCell = currentRow.getCell(synonymColumnNum - 1);
          if(ratingCell != null && ratingCell.getCellType() != Cell.CELL_TYPE_BLANK) {
            currentNode.setMaxRating(ratingCell.getNumericCellValue());
          } else {
            currentNode.setMaxRating(0.0);
          }
          Cell synCell = currentRow.getCell(synonymColumnNum);
          if(synCell != null && synCell.getCellType() != Cell.CELL_TYPE_BLANK) {
            synonymList = parseSynonyms(synCell.getStringCellValue());
          }
          Cell modusCell = currentRow.getCell(synonymColumnNum + 1);
          if(modusCell != null && modusCell.getCellType() != Cell.CELL_TYPE_BLANK) {
            properties.setModus(Modus.valueOf(modusCell.getStringCellValue()));
          }
        }
      }
      if(key != null) {
        // key must be added to list in every case
        synonymList.add(key);
        currentNode.setSynonyms(synonymList);
      }
    }
    fileInStream.close();
    workbook.close();
    return properties;
  }

  /**
   * Validates if a given meta file is correct. This is only a rough check.
   *
   * @param metaPath
   *          path to meta file
   * @return true if valid, else false
   * @throws IOException
   */
  public boolean validate(String metaPath) throws IOException {
    boolean valid = true;
    resetCounter();
    FileInputStream fileInStream = new FileInputStream(new File(metaPath));
    HSSFWorkbook workbook = new HSSFWorkbook(fileInStream);
    HSSFSheet sheet = workbook.getSheetAt(0);

    int synonymColumnNum = -1;
    boolean firstRow = true;
    boolean secondRow = true;

    for(Iterator<Row> rowIterator = sheet.iterator(); rowIterator.hasNext();) {
      Row currentRow = rowIterator.next();
      Iterator<Cell> cellIterator = currentRow.cellIterator();
      if(firstRow) {
        // -2 because of zero counting start and last row is modus
        synonymColumnNum = currentRow.getLastCellNum() - 2;
        firstRow = false;
      } else {
        if(cellIterator.hasNext()) {
          Cell optionalCell = currentRow.getCell(synonymColumnNum - 2);
          if(optionalCell != null && optionalCell.getCellType() != Cell.CELL_TYPE_BLANK
              && !"YES".equalsIgnoreCase(optionalCell.getStringCellValue()))
            valid = false;

          Cell ratingCell = currentRow.getCell(synonymColumnNum - 1);
          if(ratingCell != null && ratingCell.getCellType() != Cell.CELL_TYPE_BLANK) {
            // throws exception if string
            ratingCell.getNumericCellValue();
          }
          Cell modusCell = currentRow.getCell(synonymColumnNum + 1);
          if(secondRow) {
            secondRow = false;
            if(modusCell == null || modusCell.getCellType() == Cell.CELL_TYPE_BLANK
                || Modus.valueOf(modusCell.getStringCellValue()) == null) {
              valid = false;
            }
          }
        }
      }
    }
    fileInStream.close();
    workbook.close();
    return valid;

  }

  /**
   * Write a excel file which contains an overview of the achieved points per
   * tree and overall achieved points. Also the state of TreeNodes is displayed
   * (correct, partially correct, wrong and missing).
   *
   * @param outputFile
   *          path where output file will be created
   * @param inputRoots
   *          user input roots
   * @param solutionRoots
   *          solution roots
   * @throws IOException
   */
  public void writeEvaluation(String outputFile, List<TreeNode> inputRoots, List<TreeNode> solutionRoots)
      throws IOException {
    HSSFWorkbook workbook = new HSSFWorkbook();
    Sheet excelSheet = workbook.createSheet("Results");
    createContent(excelSheet, inputRoots, solutionRoots);
    FileOutputStream fileOut = new FileOutputStream(outputFile);
    workbook.write(fileOut);
    fileOut.close();
    workbook.close();
  }

  private void addLabel(Sheet excelSheet, int column, int row, Double val) {
    Row sheetRow = excelSheet.getRow(row);
    Cell cell = sheetRow.createCell(column);
    cell.setCellValue(val);
    // sheetRow.createCell(column).setCellValue(val);
  }

  private void addLabel(Sheet excelSheet, int column, int row, String val) {
    Row sheetRow = excelSheet.getRow(row);
    if(sheetRow == null) {
      sheetRow = excelSheet.createRow(row);
    }
    Cell cell = sheetRow.createCell(column);
    cell.setCellValue(val);
    // sheetRow.createCell(column).setCellValue(val);
  }

  private void createContent(Sheet excelSheet, List<TreeNode> inputRoots, List<TreeNode> solutionRoots) {
    prepareForFilling(excelSheet, inputRoots, solutionRoots);
    createResult(excelSheet, inputRoots);
    separateViews(excelSheet);
    createResult(excelSheet, solutionRoots);
  }

  private void createEmptyMetaContent(Sheet excelSheet, List<TreeNode> solutionRoots) {
    resetCounter();
    createEmptyMetaTrees(excelSheet, solutionRoots);
    createMetaDataHeader(excelSheet);
  }

  private void createEmptyMetaTrees(Sheet excelSheet, List<TreeNode> solutionRoots) {
    int column = col;
    // calculate max depth of trees beforehand in order to fill it immediately
    maxDepthTrees = util.getMaxDepthOfTrees(solutionRoots);
    // traverse all trees recursive and separate them with an empty row
    for(TreeNode root: solutionRoots) {
      if(!root.isMetaNode()) {
        traverseEmptyMetaTree(excelSheet, root, column);
        row++;
      }
    }
  }

  private void createMetaContent(Sheet excelSheet, List<TreeNode> solutionRoots) {
    resetCounter();
    createMetaTrees(excelSheet, solutionRoots);
    createMetaDataHeader(excelSheet);
  }

  private void createMetaDataHeader(Sheet excelSheet) {
    addLabel(excelSheet, maxDepthTrees + 1, 0, "Optional");
    addLabel(excelSheet, maxDepthTrees + 2, 0, "Bewertung");
    addLabel(excelSheet, maxDepthTrees + 3, 0, "Synonyme");
    addLabel(excelSheet, maxDepthTrees + 4, 0, "Modus");
  }

  private void createMetaTrees(Sheet excelSheet, List<TreeNode> solutionRoots) {
    // calculate max depth of trees beforehand in order to fill it immediately
    maxDepthTrees = util.getMaxDepthOfTrees(solutionRoots);
    int column = col;
    // traverse all trees recursive and separate them with an empty row
    for(TreeNode root: solutionRoots) {
      if(!root.isMetaNode()) {
        traverseMetaTree(excelSheet, root, column);
        row++;
      }
    }
  }

  private void createResult(Sheet excelSheet, List<TreeNode> roots) {
    int column = col;
    PointsResult overallPointResult = new PointsResult();
    for(TreeNode root: roots) {
      if(!root.isMetaNode()) {
        traverseTree(excelSheet, root, column);
        PointsResult pr = showTreeResult(excelSheet, root);
        overallPointResult.addToMaxPoints(pr.getMaxPoints());
        overallPointResult.addToRealPoints(pr.getRealPoints());
        // mindMap root will show the result of a single tree
        root.setMaxRating(pr.getMaxPoints());
        root.setRealRating(pr.getRealPoints());
        // visually separate trees
        row++;
      }
    }
    // visually separate overall result from trees
    row++;
    if(overallPointResult.getMaxPoints() != 0.0) {
      addLabel(excelSheet, maxDepthTrees + 3, row,
          "Overall result: " + overallPointResult.getRealPoints() + "/" + overallPointResult.getMaxPoints() + " = "
              + ((overallPointResult.getRealPoints() / overallPointResult.getMaxPoints()) * 100) + "%");
    } else {
      addLabel(excelSheet, maxDepthTrees + 3, row, "Overall result: 0/0");
    }
  }

  private TreeNode getCurrentTreeNode(String nodeTextToSearch, List<TreeNode> solutionRoots) {
    TreeNode res = null;
    for(TreeNode root: solutionRoots) {
      res = traverseTree(nodeTextToSearch, root);
      if(res != null) {
        return res;
      }
    }
    return res;
  }

  private LinkedList<String> parseSynonyms(String toParse) {
    LinkedList<String> synonyms = new LinkedList<>();
    String[] synArr = toParse.split(";");
    for(String synonym: synArr) {
      synonym = synonym.trim();
      synonyms.add(synonym);
    }
    return synonyms;
  }

  private void prepareForFilling(Sheet excelSheet, List<TreeNode> inputRoots, List<TreeNode> solutionRoots) {
    resetCounter();
    setMaxDepth(inputRoots, solutionRoots);
    addLabel(excelSheet, 0, row, "Evaluate with input:");
    addLabel(excelSheet, maxDepthTrees + 1, row, "Result");
    addLabel(excelSheet, maxDepthTrees + 2, row, "max. points");
    addLabel(excelSheet, maxDepthTrees + 3, row, "your points");
  }

  private void resetCounter() {
    col = 0;
    row = 0;
    maxDepthTrees = 0;
  }

  private void resetNodeValues(TreeNode treeNode) {
    treeNode.setMaxRating(0.0);
    treeNode.setRealRating(0.0);
    treeNode.setOptional(false);
    treeNode.setSynonyms(new LinkedList<String>());
    treeNode.addSynonym(treeNode.getText());
  }

  private void separateViews(Sheet excelSheet) {
    row++;
    row++;
    row++;
    addLabel(excelSheet, 0, row, "Evaluate with solution:");
  }

  private void setMaxDepth(List<TreeNode> inputRoots, List<TreeNode> solutionRoots) {
    int depthIn = util.getMaxDepthOfTrees(inputRoots);
    int depthSol = util.getMaxDepthOfTrees(solutionRoots);
    if(depthIn < depthSol) {
      maxDepthTrees = depthSol;
    } else {
      maxDepthTrees = depthIn;
    }
  }

  private PointsResult showTreeResult(Sheet excelSheet, TreeNode root) {
    PointsResult pr = Util.getSingleTreePoints(root);
    row++;
    if(pr.getMaxPoints() != 0.0) {
      addLabel(excelSheet, maxDepthTrees + 3, row, "Tree result: " + pr.getRealPoints() + "/" + pr.getMaxPoints()
          + " = " + ((pr.getRealPoints() / pr.getMaxPoints()) * 100) + "%");
    } else {
      addLabel(excelSheet, maxDepthTrees + 3, row, "Tree result: 0/0");
    }
    return pr;
  }

  private void traverseEmptyMetaTree(Sheet excelSheet, TreeNode treeNode, int column) {
    int colNumber = column;
    row++;
    addLabel(excelSheet, colNumber, row, treeNode.getText());
    resetNodeValues(treeNode);
    colNumber++;
    for(TreeNode child: treeNode.getChildren()) {
      traverseEmptyMetaTree(excelSheet, child, colNumber);
    }
  }

  private void traverseMetaTree(Sheet excelSheet, TreeNode treeNode, int column) {
    int colNumber = column;
    row++;
    addLabel(excelSheet, colNumber, row, treeNode.getText());
    // only the first nodes highest in the hierarchy are set to "yes" if they
    // are optional
    if(treeNode.getParent() == null) {
      if(treeNode.isOptional()) {
        addLabel(excelSheet, maxDepthTrees + 1, row, "YES");
      }
    } else {
      if(!treeNode.getParent().isOptional() && treeNode.isOptional()) {
        addLabel(excelSheet, maxDepthTrees + 1, row, "YES");
      }
    }
    if(treeNode.getMaxRating() != 0.0) {
      addLabel(excelSheet, maxDepthTrees + 2, row, treeNode.getMaxRating());
    }
    if(treeNode.getSynonyms().size() > 1) {
      String synonyms = treeNode.getSynonyms().stream().filter(s -> !s.equals(treeNode.getText()))
          .collect(Collectors.joining(";"));
      addLabel(excelSheet, maxDepthTrees + 3, row, synonyms);
    }
    colNumber++;
    for(TreeNode child: treeNode.getChildren()) {
      traverseMetaTree(excelSheet, child, colNumber);
    }
  }

  private void traverseTree(Sheet excelSheet, TreeNode treeNode, int column) {
    int colNumber = column;
    row++;
    addLabel(excelSheet, colNumber, row, treeNode.getText());
    addLabel(excelSheet, maxDepthTrees + 1, row, treeNode.getDifferenceResult().toString());
    addLabel(excelSheet, maxDepthTrees + 2, row, treeNode.getMaxRating());
    addLabel(excelSheet, maxDepthTrees + 3, row, treeNode.getRealRating());
    colNumber++;
    for(TreeNode child: treeNode.getChildren()) {
      traverseTree(excelSheet, child, colNumber);
    }
  }

  private TreeNode traverseTree(String nodeTextToSearch, TreeNode currentNode) {
    TreeNode res = null;
    if(currentNode.getText().equals(nodeTextToSearch)) {
      return currentNode;
    } else {
      for(TreeNode child: currentNode.getChildren()) {
        res = traverseTree(nodeTextToSearch, child);
        if(res != null) {
          return res;
        }
      }
    }
    return res;
  }
}
