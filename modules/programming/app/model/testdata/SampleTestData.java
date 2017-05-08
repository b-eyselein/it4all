package model.testdata;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class SampleTestData extends ITestData {

  public static final Finder<SampleTestDataKey, SampleTestData> finder = new Finder<>(SampleTestData.class);

  @EmbeddedId
  public SampleTestDataKey key;

  public SampleTestData(SampleTestDataKey theKey) {
    key = theKey;
  }

  @Override
  public int getId() {
    return key.testId;
  }
}