package model;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import play.Logger;

public class XmlCorrector {

  private static final DocumentBuilderFactory DOC_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
  private static final SchemaFactory SCHEMA_FACTORY = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI); // NOSONAR

  private XmlCorrector() {
    DOC_BUILDER_FACTORY.setValidating(true);
  }

  public static List<XmlError> correct(String xml, String grammar, XmlExType exType) {
    StringReader xmlReader = new StringReader(xml);
    StringReader grammarReader = new StringReader(grammar);

    switch(exType) {
    case XML_XSD:
      return correctXMLAgainstXSD(xmlReader, grammarReader, new CorrectionErrorHandler());
    case XML_DTD:
    case DTD_XML:
      return correctWithDTD(xmlReader, new CorrectionErrorHandler());
    case XSD_XML:
    default:
      return Collections.emptyList();
    }

  }

  private static List<XmlError> correctWithDTD(StringReader xml, CorrectionErrorHandler errorHandler) {
    try {
      DocumentBuilder builder = DOC_BUILDER_FACTORY.newDocumentBuilder();
      builder.setErrorHandler(errorHandler);
      builder.parse(new InputSource(xml));

    } catch (ParserConfigurationException e) {
      Logger.error("There was an error creating the Parser: ", e);
    } catch (SAXException | IOException e) { // NOSONAR
    }
    return errorHandler.getErrors();
  }

  private static List<XmlError> correctXMLAgainstXSD(StringReader xml, StringReader grammar,
      CorrectionErrorHandler errorHandler) {
    Source xmlFile = new StreamSource(xml);
    Source xsdFile = new StreamSource(grammar);

    try {
      Schema schema = SCHEMA_FACTORY.newSchema(xsdFile);

      if(schema == null)
        return Arrays
            .asList(new XmlError("Ihre Eingabedaten konnten nicht geladen werden!", -1, XmlErrorType.FATALERROR));

      Validator validator = schema.newValidator();
      validator.setErrorHandler(errorHandler);
      validator.validate(xmlFile);
    } catch (SAXException | IOException e) { // NOSONAR
      // Errors are getting caught in error handler since they are made by
      // learners
    } catch (NullPointerException e) {
      Logger.error("Could not validate XSD-File", e);
      return Arrays.asList(new XmlError("Konnte XSD nicht validieren.", -1, XmlErrorType.FATALERROR));
    }

    return errorHandler.getErrors();
  }

}
