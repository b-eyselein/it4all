package model;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class FilloutQuestion extends BooleanQuestion {
  
  // @formatter:off
  private static final Map<String, String> HTML_REPLACERS = new ImmutableMap.Builder<String, String>()
      .put("IMPL", "&rArr;")
      .put("NOR", "&#x22bd;")
      .put("NAND", "&#x22bc;")
      .put("EQUIV", "&hArr;")
      .put("NOT", "&not;")
      .put("AND", "&and;")
      .put("XOR", "&oplus;")
      .put("OR", "&or;")
      .build();
  // @formatter:on
  
  private final ScalaNode formula;
  
  private final List<Assignment> assignments;
  
  public FilloutQuestion(ScalaNode theFormulaTree) {
    super(theFormulaTree.getUsedVariables());
    formula = theFormulaTree;
    assignments = Assignment.generateAllAssignments(variables);
  }
  
  public static FilloutQuestion generateNew() {
    return new FilloutQuestion(BoolFormulaGenerator.generateRandom());
  }
  
  public List<Assignment> getAssignments() {
    return assignments;
  }
  
  public ScalaNode getFormula() {
    return formula;
  }
  
  public String getFormulaAsHtml() {
    String formulaAsHtml = formula.toString();
    
    for(Map.Entry<String, String> replacer: HTML_REPLACERS.entrySet())
      formulaAsHtml = formulaAsHtml.replaceAll(replacer.getKey(), replacer.getValue());
    
    return formulaAsHtml;
  }
  
  public String getFormulaAsString() {
    return formula.toString();
  }
  
  public boolean isCorrect() {
    return assignments.parallelStream()
        .allMatch(a -> a.isSet(LEARNER_VARIABLE) && a.get(LEARNER_VARIABLE) == a.get(SOLUTION_VARIABLE));
  }
  
}
