package model.html;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import model.Corrector;
import uniwue.html.parser.Parser;
import uniwue.html.parser.result.FailureParseResult;
import uniwue.html.parser.result.HtmlParsingError;
import uniwue.html.parser.result.ParseResult;

public class HtmlCorrector extends Corrector<HtmlExercise> {
  
  private Parser parser = new Parser();
  
  public HtmlCorrector(HtmlExercise exercise) {
    super(exercise);
  }
  
  public List<HtmlParsingError> tryParsing(String solution) {
    ParseResult parseResult = parser.parse(solution);
    if(parseResult.parseWasSuccessful())
      return Collections.emptyList();
    else
      return ((FailureParseResult) parseResult).getErrors();
  }
  
  @Override
  public List<String> correct(String solution) {
    List<HtmlParsingError> errors = tryParsing(solution);
    System.out.println("Errors: " + errors.isEmpty());
    if(!errors.isEmpty())
      for(HtmlParsingError err: errors)
        System.out.println(err);
    if(errors.isEmpty())
      return Arrays.asList("Keine Fehler");
    else
      return errors.stream().map(error -> error.toString()).collect(Collectors.toList());
  }
  
}
