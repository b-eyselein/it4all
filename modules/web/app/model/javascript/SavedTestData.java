package model.javascript;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class SavedTestData implements ITestData {

  private static final String VALUES_SPLIT_CHAR = "#";

  @Column(columnDefinition = "text")
  public String inputs;

  @Column(columnDefinition = "text")
  public String datatypes;

  public String output;

  @Override
  public List<String> getDataTypes() {
    return Arrays.asList(datatypes.split(VALUES_SPLIT_CHAR));
  }

  @Override
  public List<String> getInput() {
    return Arrays.asList(inputs.split(VALUES_SPLIT_CHAR));
  }

  @Override
  public String getOutput() {
    return output;
  }

}
