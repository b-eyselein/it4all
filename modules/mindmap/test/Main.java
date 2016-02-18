

//import java.io.File;
//import java.io.IOException;
//import java.util.LinkedList;

//import parser.AbstractEvaluationParser;
//import parser.ParserFactory;
//import basics.TreeNode;
import evaluation.Evaluation;
//import evaluation.Properties;
//import evaluation.RWExcel;
import evaluation.Validation;
//import evaluation.enums.MetaDataState;

//import java.io.File;
//import java.io.IOException;
//import java.util.LinkedList;
//
//import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
//import org.apache.xmlbeans.XmlException;
//
//import basics.TreeNode;
//import parser.WordParser;

//import testing.Test;

public class Main {

	public static void main(String[] args) {
//		Test test = new Test();
//		
//		test.abstractXToX("mindmanager", "latex", "C:/Users/Magnus/Desktop/xTOx/SOLUTION.xml", "C:/Users/Magnus/Desktop/xTOx/resultLatex.tex", "latex");
//		test.abstractXToX("mindmanager", "mindmanager", "C:/Users/Magnus/Desktop/xTOx/SOLUTION.xml", "C:/Users/Magnus/Desktop/xTOx/resultMM.xml", "mindmanager");
//		test.abstractXToX("mindmanager", "word", "C:/Users/Magnus/Desktop/xTOx/SOLUTION.xml", "C:/Users/Magnus/Desktop/xTOx/resultWord.docx", "word");
		
		try {
			String solution = "Evaluation/Test_Task/SOLUTION.xml";
			String input = "Evaluation/Test_Task/TASK_DONE.xml";
			String meta = "Evaluation/Test_Task/META.xls";
			String template = "Evaluation/Test_Task/TEMPLATE.mmas";
			
			String result = "Evaluation/Test_Task/Results/RESULT.xls";
			String alteredSolution = "Evaluation/Test_Task/Results/ALTERED_SOLUTION.xml";
			String alteredInput = "Evaluation/Test_Task/Results/ALTERED_INPUT.xml";
			
			Validation.validateMindMap(solution);
			Validation.validateMindMap(input);
			
			//if false: stop here and let admin edit the meta file
			//afterwards continue here or start again
			Validation.checkForMeta(solution, meta);
			Validation.validateMeta(meta);
			
			System.out.println("Validation done.");
			
			Evaluation.evaluate("MINDMANAGER", input, solution, result, alteredSolution, alteredInput, meta, template);
			
			System.out.println("Evaluation done.");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
//		WordParser wp2 = new WordParser();
//		try {
//			LinkedList<TreeNode> asd = wp2.read(new File("C:/Users/Magnus/Desktop/test.docx"));
//			wp2.write("C:/Users/Magnus/Desktop/write.docx", asd, "C:/Users/Magnus/Desktop/TOC_TEMPLATE.dotx");
//		} catch (InvalidFormatException | IOException e) {
//			e.printStackTrace();
//		} catch (XmlException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		//file transformations
//		test.freePlanToFreePlane();
//		test.freePlaneToLatex();
//		test.freePlaneToMindManager();
//		test.freePlaneToWord();
//		test.latexToFreePlane();
//		test.latexToLatex();
//		test.latexToMindManager();
//		test.latexToWord();
//		test.mindManagerToFreePlane();
//		test.mindManagerToLatex();
//		test.mindManagerToMindManager();
//		test.mindManagerToWord();
//		test.wordToFreePlane();
//		test.wordToLatex();
//		test.wordToMindManager();
//		test.wordToWord();
		
		//show difference between mind-maps
		//MindManager
//		test.evaluateMultipleRootMapsWithExcel("mindmanager",
//				"C:/Users/Magnus/Dropbox/workspaceDropbox/java/Results/MultipleRoots/MindManager/input.xml",
//				"C:/Users/Magnus/Dropbox/workspaceDropbox/java/Results/MultipleRoots/MindManager/solutionWithMetaData.xml",
//				"Results/MultipleRoots/MindManager/result.xls",
//				"Results/MultipleRoots/alteredSolution.xml",
//				"Results/MultipleRoots/alteredInput.xml",
//				"Results/MultipleRoots/metaData.xls",
//				"Results/MindManager/Vorlage.mmas");
		//FreePlane
//		test.evaluateMultipleRootMapsWithExcel("freeplane",
//				"Results/MultipleRoots/FreePlane/input.mm",
//				"Results/MultipleRoots/FreePlane/solution.mm",
//				"Results/MultipleRoots/FreePlane/result.xls",
//				"Results/MultipleRoots/metaData.xls",
//				null);
	}
}
