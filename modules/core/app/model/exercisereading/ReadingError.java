package model.exercisereading;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;

public class ReadingError extends AbstractReadingResult {

  private ProcessingReport report;

  public ReadingError(String theJson, String theJsonSchema, ProcessingReport theReport) {
    super(theJson, theJsonSchema);
    report = theReport;
  }

  public List<String> getErrors() {
    return StreamSupport.stream(report.spliterator(), true).map(ProcessingMessage::toString)
        .collect(Collectors.toList());
  }

  public ProcessingReport getReport() {
    return report;
  }

  @Override
  public boolean isSuccess() {
    return false;
  }

}
