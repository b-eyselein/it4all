package model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import model.result.EvaluationResult;

public class EmptyXMLTest {

  @Test
  public void emptyXmlAgainstXSD() {
    Path emptyXML = Paths.get("test", "resources", "empty.xml");
    Path xsd = Paths.get("test", "resources", "note.xsd");
    List<EvaluationResult> out = XmlCorrector.correctXMLAgainstXSD(emptyXML, xsd);
    assertThat("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), equalTo(1));

    XMLError error = (XMLError) out.get(0);

    assertThat(error.getErrorType(), equalTo(XmlErrorType.FATALERROR));
    assertThat(error.getErrorMessage(), equalTo("Premature end of file."));
  }

  @Test
  public void xmlNoElement() {
    Path xml = Paths.get("test", "resources", "xmlNoElement.xml");
    List<EvaluationResult> out = XmlCorrector.correctXMLAgainstDTD(xml);
    assertThat("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), equalTo(1));

    XMLError error = (XMLError) out.get(0);

    assertThat(error.getErrorType(), equalTo(XmlErrorType.FATALERROR));
    assertThat(error.getErrorMessage(), equalTo("Premature end of file."));
  }

}
