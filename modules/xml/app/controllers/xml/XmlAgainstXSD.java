package controllers.xml;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

 
public class XmlAgainstXSD {
	public static void main(String[] args) throws IOException {
		final List<String> output = new LinkedList<>();
		Source schemaFile = new StreamSource(new File("//home/shpend//Downloads//books.xsd"));

		Source xmlFile = new StreamSource(new File("//home/shpend//Downloads//xmlFile.xml"));
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = null;
		try {
			schema = schemaFactory.newSchema(schemaFile);
		} catch (SAXException e) {
		}

		Validator validator = schema.newValidator();
		validator.setErrorHandler(new ErrorHandler() {

			@Override
			public void warning(SAXParseException exception) throws SAXException {
				String string = "WARNING:" + "\n" + "SystemID: " + exception.getSystemId() + "\n" + "Zeile: "
						+ exception.getLineNumber() + "\n" + "Spalte: " + exception.getColumnNumber() + "\n" + "Fehler"
						+ exception.getMessage() + "\n";
				output.add(string);
			}
			@Override
			public void fatalError(SAXParseException exception) throws SAXException {
				String string = "FATAL ERROR:" + "\n" + "SystemID: " + exception.getSystemId() + "\n" + "Zeile: "
						+ exception.getLineNumber() + "\n" + "Spalte: " + exception.getColumnNumber() + "\n"
						+ "Fehler: " + exception.getMessage() + "\n";
				output.add(string);
			}
			@Override
			public void error(SAXParseException exception) throws SAXException {
				String string = "ERROR:" + "\n" + "SystemID: " + exception.getSystemId() + "\n" + "Zeile: "
						+ exception.getLineNumber() + "\n" + "Spalte: " + exception.getColumnNumber() + "\n" + "Fehler"
						+ exception.getMessage() + "\n";
				output.add(string);
			}
		});
		try {
			validator.validate(xmlFile);
		} catch (SAXException e) {
		}

		if (!output.isEmpty()) {
			for (String item : output) {
				System.out.println(item);
			}
		} else {
			System.out.println("Keine Fehler");
		}
	}

}
