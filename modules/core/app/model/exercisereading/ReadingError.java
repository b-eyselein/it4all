package model.exercisereading;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;

public class ReadingError extends AbstractReadingResult {

  private ProcessingReport report;
  private String message;

  public ReadingError(String theJson, String theJsonSchema, ProcessingReport theReport) {
    super(theJson, theJsonSchema);
    report = Objects.requireNonNull(theReport);
  }

  public ReadingError(String theJson, String theJsonSchema, String theMessage) {
    super(theJson, theJsonSchema);
    message = theMessage;
  }

  public List<String> getErrors() {
    if(report == null)
      return Arrays.asList(message);
    
    return StreamSupport.stream(report.spliterator(), true).filter(Objects::nonNull).map(ProcessingMessage::toString)
        .collect(Collectors.toList());
  }

  @Override
  public boolean isSuccess() {
    return false;
  }

}
