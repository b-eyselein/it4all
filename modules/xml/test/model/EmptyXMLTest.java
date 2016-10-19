package model;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import java.io.File;
import java.util.List;

import org.junit.Test;

import model.exercise.EvaluationResult;

public class EmptyXMLTest {

  @Test
  public void emptyXmlAgainstXSD() {
    File emptyXML = new File("test/resources/empty.xml");
    File xsd = new File("test/resources/note.xsd");
    List<EvaluationResult> out = XmlCorrector.correctXMLAgainstXSD(emptyXML, xsd);
    assertThat("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), equalTo(1));

    XMLError error = (XMLError) out.get(0);

    assertThat(error.getErrorType(), equalTo(XmlErrorType.FATALERROR));
    assertThat(error.getErrorMessage(), equalTo("Premature end of file."));
  }

  @Test
  public void xmlNoElement() {
    File xml = new File("test/resources/xmlNoElement.xml");
    List<EvaluationResult> out = XmlCorrector.correctXMLAgainstDTD(xml);
    assertThat("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), equalTo(1));

    XMLError error = (XMLError) out.get(0);

    assertThat(error.getErrorType(), equalTo(XmlErrorType.FATALERROR));
    assertThat(error.getErrorMessage(), equalTo("Premature end of file."));
  }

}
