package model.bool;

import java.util.AbstractMap.SimpleEntry;

import model.bool.tree.Assignment;
import model.bool.tree.BoolescheFunktionTree;

import java.util.Arrays;
import java.util.List;

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

  public static FilloutQuestion generateNew() {
    // TODO: implement!
    BoolescheFunktionTree bft = BoolescheFunktionenGenerator.neueBoolescheFunktion();
    String formel = bft.toString();

    if(formel.length() > 25) {
      String alternative_formel_1 = bft.kurzeDisjunktiveNormalform();
      String alternative_formel_2 = bft.kurzeKonjunktiveNormalform();
      if(alternative_formel_1.length() < alternative_formel_2.length()
          && alternative_formel_1.length() < formel.length()) {
        formel = alternative_formel_1;
      } else if(alternative_formel_2.length() < alternative_formel_1.length()
          && alternative_formel_2.length() < formel.length()) {
        formel = alternative_formel_2;
      }
    }

    return new FilloutQuestion(bft.getVariablen(), bft);
  }

  private BoolescheFunktionTree formula;
  private List<Assignment> assignments;

  public FilloutQuestion(char[] theVariables, BoolescheFunktionTree theFormulaTree) {
    super(theVariables);
    formula = theFormulaTree;
    assignments = Assignment.generateAllAssignments(variables);
  }

  public List<Assignment> getAssignments() {
    return assignments;
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

}
