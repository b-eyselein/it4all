package model;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;

import model.tree.Assignment;
import model.tree.BoolescheFunktionTree;
import play.twirl.api.Html;

public class FilloutQuestion extends BooleanQuestion {

  // @formatter:off
  private static final List<SimpleEntry<String, String>> replacers = Arrays.asList(
      new SimpleEntry<>("IMPL", "&rArr;"),
      new SimpleEntry<>("NOR", "&#x22bd;"),
      new SimpleEntry<>("NAND", "&#x22bc;"),
      new SimpleEntry<>("EQUIV", "&hArr;"),
      new SimpleEntry<>("NOT", "&not;"),
      new SimpleEntry<>("AND", "&and;"),
      new SimpleEntry<>("XOR", "&oplus;"),
      new SimpleEntry<>("OR", "&or;"));
  // @formatter:on
  
  private BoolescheFunktionTree formula;
  
  private List<Assignment> assignments;
  
  public FilloutQuestion(Character[] theVariables, BoolescheFunktionTree theFormulaTree) {
    super(theVariables);
    formula = theFormulaTree;
    assignments = Assignment.generateAllAssignments(variables);
  }
  
  public static FilloutQuestion generateNew() {
    // TODO: implement!
    BoolescheFunktionTree bft = BoolescheFunktionenGenerator.generateRandomBooleanFunction();
    return new FilloutQuestion(bft.getVariables(), bft);
  }
  
  public List<Assignment> getAssignments() {
    return assignments;
  }

  public BoolescheFunktionTree getFormula() {
    return formula;
  }
  
  public Html getFormulaAsHtml() {
    String formulaAsHtml = formula.toString();
    for(SimpleEntry<String, String> replacer: replacers)
      formulaAsHtml = formulaAsHtml.replaceAll(replacer.getKey(), replacer.getValue());
    return new Html(formulaAsHtml);
  }
  
  public String getFormulaAsString() {
    return formula.toString();
  }
  
  @Override
  public int getId() {
    // TODO Auto-generated method stub
    return 0;
  }
  
  @Override
  public int getMaxPoints() {
    // TODO Auto-generated method stub
    return 0;
  }
  
  @Override
  public String getText() {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public String getTitle() {
    // TODO Auto-generated method stub
    return null;
  }
  
  public boolean isCorrect() {
    for(Assignment assignment: assignments)
      if(!assignment.assignmentIsSet(LEARNER_VARIABLE)
          || assignment.getAssignment(LEARNER_VARIABLE) != assignment.getAssignment(SOLUTION_VARIABLE))
        return false;
    return true;
  }
  
}
