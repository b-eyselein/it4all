//import java.io.File;
//import java.io.IOException;
//import java.util.LinkedList;
//
//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.transform.TransformerException;
//
//import TreeNode;
//import MultipleRoots;
//import Util;
//import AbstractParser;
//import FreePlanParser;
//import LatexParser;
//import MindManagerParser;
//import model.mindmap.parser.ParserFactory;
//import WordParser;
//
//import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
//import org.apache.xmlbeans.XmlException;
//import org.xml.sax.SAXException;

public class Test {
  
  // private static final String FREEPLANE_TEMPLATE = null;
  // private static final String LATEX_TEMPLATE = "Results/Latex/preamble.tex";
  // private static final String MINDMANAGER_TEMPLATE =
  // "Results/MindManager/Vorlage.mmas";
  // private static final String WORD_TEMPLATE = "Results/Word/Vorlage.dotx";
  
  // public void abstractXToX(String readParser, String writeParser, String
  // readPath, String writePath, String template) {
  // try {
  // AbstractParser r = ParserFactory.getTOCParser(readParser);
  // LinkedList<TreeNode> listOfRoots = r.read(new File(readPath));
  // listOfRoots = Util.mergeTrees(listOfRoots);
  // AbstractParser w = ParserFactory.getTOCParser(writeParser);
  // w.write(writePath, listOfRoots, getTemplate(template));
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  // }
  //
  // private String getTemplate(String template) {
  // switch (template.toUpperCase()) {
  // case "LATEX":
  // return "C:/Users/Magnus/Desktop/xTOx/TOC_TEMPLATE.tex";
  // case "MINDMANAGER":
  // return "C:/Users/Magnus/Desktop/xTOx/TOC_TEMPLATE.mmas";
  // case "WORD":
  // return "C:/Users/Magnus/Desktop/xTOx/TOC_TEMPLATE.dotx";
  // default:
  // return null;
  // }
  // }
  //
  // public void freePlanToFreePlane() {
  // try {
  // FreePlanParser fpp = new FreePlanParser();
  // LinkedList<TreeNode> listOfRoots = fpp.read(new
  // File("C:/Users/Magnus/Dropbox/workspaceDropbox/java/Results/Freeplane/Eingabe.mm"));
  // listOfRoots = Util.mergeTrees(listOfRoots);
  // fpp.write("Results/Freeplane/F2F/Ausgabe.mm", listOfRoots,
  // FREEPLANE_TEMPLATE);
  // } catch (ParserConfigurationException | SAXException | IOException e) {
  // e.printStackTrace();
  // } catch (TransformerException e) {
  // e.printStackTrace();
  // }
  // }
  //
  // public void freePlaneToLatex() {
  // try {
  // FreePlanParser fpp = new FreePlanParser();
  // LinkedList<TreeNode> listOfRoots = fpp.read(new
  // File("C:/Users/Magnus/Dropbox/workspaceDropbox/java/Results/Freeplane/Eingabe.mm"));
  //
  // listOfRoots = Util.mergeTrees(listOfRoots);
  // LatexParser lp = new LatexParser();
  // lp.write("Results/Freeplane/F2L/Ausgabe.tex", listOfRoots, LATEX_TEMPLATE);
  // } catch (ParserConfigurationException | SAXException | IOException e) {
  // e.printStackTrace();
  // }
  // }
  //
  // public void freePlaneToMindManager() {
  // try {
  // FreePlanParser fpp = new FreePlanParser();
  // LinkedList<TreeNode> listOfRoots = fpp.read(new
  // File("C:/Users/Magnus/Dropbox/workspaceDropbox/java/Results/Freeplane/Eingabe.mm"));
  // listOfRoots = Util.mergeTrees(listOfRoots);
  // MindManagerParser mmp = new MindManagerParser();
  // mmp.write("Results/Freeplane/F2M/Ausgabe.xml", listOfRoots,
  // MINDMANAGER_TEMPLATE);
  // } catch (ParserConfigurationException | SAXException | IOException e) {
  // e.printStackTrace();
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  // }
  //
  // public void freePlaneToWord() {
  // try {
  // FreePlanParser fpp = new FreePlanParser();
  // LinkedList<TreeNode> listOfRoots = fpp.read(new
  // File("C:/Users/Magnus/Dropbox/workspaceDropbox/java/Results/Freeplane/Eingabe.mm"));
  // listOfRoots = Util.mergeTrees(listOfRoots);
  // WordParser wp = new WordParser();
  // wp.write("Results/Freeplane/F2W/Ausgabe.docx", listOfRoots, WORD_TEMPLATE);
  // } catch (ParserConfigurationException | SAXException | IOException |
  // XmlException e) {
  // e.printStackTrace();
  // }
  // }
  //
  // public void latexToFreePlane() {
  // try {
  // LatexParser lp = new LatexParser();
  // LinkedList<TreeNode> listOfRoots = lp.read(new
  // File("C:/Users/Magnus/Dropbox/workspaceDropbox/java/Results/Latex/Eingabe.tex"));
  // listOfRoots = Util.mergeTrees(listOfRoots);
  // FreePlanParser fpp = new FreePlanParser();
  // fpp.write("Results/Latex/L2F/Ausgabe.mm", listOfRoots, FREEPLANE_TEMPLATE);
  // } catch (ParserConfigurationException | TransformerException e) {
  // e.printStackTrace();
  // } catch (IOException e) {
  // e.printStackTrace();
  // }
  // }
  //
  // public void latexToLatex() {
  // try {
  // LatexParser lp = new LatexParser();
  // LinkedList<TreeNode> listOfRoots = lp.read(new
  // File("C:/Users/Magnus/Dropbox/workspaceDropbox/java/Results/Latex/Eingabe.tex"));
  // listOfRoots = Util.mergeTrees(listOfRoots);
  // lp.numerateAllTrees(listOfRoots);
  // lp.write("Results/Latex/L2L/Ausgabe.tex", listOfRoots, LATEX_TEMPLATE);
  // } catch (IOException e) {
  // e.printStackTrace();
  // }
  // }
  //
  // public void latexToMindManager() {
  // try {
  // LatexParser lp = new LatexParser();
  // LinkedList<TreeNode> listOfRoots = lp.read(new
  // File("C:/Users/Magnus/Dropbox/workspaceDropbox/java/Results/Latex/Eingabe.tex"));
  // listOfRoots = Util.mergeTrees(listOfRoots);
  // MindManagerParser mmp = new MindManagerParser();
  // mmp.write("Results/Latex/L2M/Ausgabe.xml", listOfRoots,
  // MINDMANAGER_TEMPLATE);
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  // }
  //
  // public void latexToWord() {
  // try {
  // LatexParser lp = new LatexParser();
  // LinkedList<TreeNode> listOfRoots = lp.read(new
  // File("C:/Users/Magnus/Dropbox/workspaceDropbox/java/Results/Latex/Eingabe.tex"));
  // listOfRoots = Util.mergeTrees(listOfRoots);
  // WordParser wp = new WordParser();
  // wp.write("Results/Latex/L2W/Ausgabe.docx", listOfRoots, WORD_TEMPLATE);
  // } catch (IOException | XmlException e) {
  // e.printStackTrace();
  // }
  // }
  //
  // public void mindManagerToFreePlane() {
  // try {
  // MindManagerParser mmp = new MindManagerParser();
  // LinkedList<TreeNode> listOfRoots = mmp.read(new
  // File("C:/Users/Magnus/Dropbox/workspaceDropbox/java/Results/MindManager/Eingabe.xml"));
  // listOfRoots = Util.mergeTrees(listOfRoots);
  // FreePlanParser fpp = new FreePlanParser();
  // fpp.write("Results/MindManager/M2F/Ausgabe.mm", listOfRoots,
  // FREEPLANE_TEMPLATE);
  // } catch (ParserConfigurationException | TransformerException e) {
  // e.printStackTrace();
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  // }
  //
  // public void mindManagerToLatex() {
  // try {
  // MindManagerParser mmp = new MindManagerParser();
  // LinkedList<TreeNode> listOfRoots = mmp.read(new
  // File("C:/Users/Magnus/Dropbox/workspaceDropbox/java/Results/MindManager/Eingabe.xml"));
  // listOfRoots = Util.mergeTrees(listOfRoots);
  // LatexParser lp = new LatexParser();
  // lp.write("Results/MindManager/M2L/Ausgabe.tex", listOfRoots,
  // LATEX_TEMPLATE);
  // } catch (IOException e) {
  // e.printStackTrace();
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  // }
  //
  // public void mindManagerToMindManager() {
  // try {
  // MindManagerParser mmp = new MindManagerParser();
  // LinkedList<TreeNode> listOfRoots = mmp.read(new
  // File("C:/Users/Magnus/Dropbox/workspaceDropbox/java/Results/MindManager/Eingabe.xml"));
  // listOfRoots = Util.mergeTrees(listOfRoots);
  // mmp.write("Results/MindManager/M2M/Ausgabe.xml", listOfRoots,
  // MINDMANAGER_TEMPLATE);
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  // }
  //
  // public void mindManagerToWord() {
  // try {
  // MindManagerParser mmp = new MindManagerParser();
  // LinkedList<TreeNode> listOfRoots = mmp.read(new
  // File("C:/Users/Magnus/Dropbox/workspaceDropbox/java/Results/MindManager/Eingabe.xml"));
  // listOfRoots = Util.mergeTrees(listOfRoots);
  // WordParser wp = new WordParser();
  // wp.write("Results/MindManager/M2W/Ausgabe.docx", listOfRoots,
  // WORD_TEMPLATE);
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  // }
  //
  // public void wordToFreePlane() {
  // try {
  // WordParser wp = new WordParser();
  // LinkedList<TreeNode> listOfRoots = wp.read(new
  // File("C:/Users/Magnus/Dropbox/workspaceDropbox/java/Results/Word/Eingabe.docx"));
  // listOfRoots = Util.mergeTrees(listOfRoots);
  // FreePlanParser fpp = new FreePlanParser();
  // fpp.write("Results/Word/W2F/Ausgabe.mm", listOfRoots, FREEPLANE_TEMPLATE);
  // } catch (ParserConfigurationException | TransformerException |
  // InvalidFormatException | IOException e) {
  // e.printStackTrace();
  // }
  // }
  //
  // public void wordToLatex() {
  // try {
  // WordParser wp = new WordParser();
  // LinkedList<TreeNode> listOfRoots = wp.read(new
  // File("C:/Users/Magnus/Dropbox/workspaceDropbox/java/Results/Word/Eingabe.docx"));
  // listOfRoots = Util.mergeTrees(listOfRoots);
  // LatexParser lp = new LatexParser();
  // lp.write("Results/Word/W2L/Ausgabe.tex", listOfRoots, LATEX_TEMPLATE);
  // } catch (IOException | InvalidFormatException e) {
  // e.printStackTrace();
  // }
  // }
  //
  // public void wordToMindManager() {
  // try {
  // WordParser wp = new WordParser();
  // LinkedList<TreeNode> listOfRoots = wp.read(new
  // File("C:/Users/Magnus/Dropbox/workspaceDropbox/java/Results/Word/Eingabe.docx"));
  // listOfRoots = Util.mergeTrees(listOfRoots);
  // MindManagerParser mmp = new MindManagerParser();
  // mmp.write("Results/Word/W2M/Ausgabe.xml", listOfRoots,
  // MINDMANAGER_TEMPLATE);
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  // }
  //
  // public void wordToWord() {
  // try {
  // WordParser wp = new WordParser();
  // LinkedList<TreeNode> listOfRoots = wp.read(new
  // File("C:/Users/Magnus/Dropbox/workspaceDropbox/java/Results/Word/Eingabe.docx"));
  // listOfRoots = Util.mergeTrees(listOfRoots);
  // wp.write("Results/Word/W2W/Ausgabe.docx", listOfRoots, WORD_TEMPLATE);
  // } catch (InvalidFormatException | IOException | XmlException e) {
  // e.printStackTrace();
  // }
  // }
  //
  // public void evaluateMultipleRootMapsWithExcel(String parserType, String
  // inputPathAttempt,
  // String inputPathSolution, String outputPath, String alteredSolution,
  // String alteredInput, String metaFilePath, String templatePath) {
  // try {
  // MultipleRoots mr = new MultipleRoots();
  // mr.evalMultiRootMapWithExcel(parserType, inputPathAttempt,
  // inputPathSolution, outputPath, alteredSolution,
  // alteredInput, metaFilePath, templatePath);
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  // }
}
