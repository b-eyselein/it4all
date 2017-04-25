package model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

public class NoPartyDate {

  @Test
  public void testCorrectDTDAgainstXML() {
    Path referenceFile = Paths.get("test", "resources", "partyNoDate.xml");
    List<XmlError> out = XmlCorrector.correctDTDAgainstXML(referenceFile);
    assertThat("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), equalTo(1));

    XmlError error = out.get(0);

    assertThat(error.getErrorType(), equalTo(XmlErrorType.ERROR));
    assertThat(error.getErrorMessage(), equalTo("Attribute \"datum\" must be declared for element type \"party\"."));
  }

}
