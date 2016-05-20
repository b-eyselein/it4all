package model;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
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

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class CorrectorXml {
	final List<String> output = new LinkedList<>();

	public List<String> correctXMLAgainstDTD(File studentensolutionForXML) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
		}
		builder.setErrorHandler(new ErrorHandler() {

			@Override
			public void warning(SAXParseException exception) throws SAXException {
				printWarning(exception);
			}

			@Override
			public void fatalError(SAXParseException exception) throws SAXException {
				printFatalError(exception);
			}

			@Override
			public void error(SAXParseException exception) throws SAXException {
				printError(exception);

			}
		});

		try {
			Document doc = builder.parse(studentensolutionForXML);

		} catch (SAXException e) {
		} catch (IOException e) {
		}

		return output;
	}

	public List<String> correctXMLAgainstXSD(File sampleSolution, File studentSolution) throws IOException {
		Source xmlFile = new StreamSource(studentSolution);
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = null;
		try {
			schema = schemaFactory.newSchema(sampleSolution);
		} catch (SAXException e) {
		}

		Validator validator = null;
		try {
			validator = schema.newValidator();
		} catch (NullPointerException e1) {
			e1.printStackTrace();
		}
		validator.setErrorHandler(new ErrorHandler() {

			@Override
			public void warning(SAXParseException exception) throws SAXException {
				printWarning(exception);
			}

			@Override
			public void fatalError(SAXParseException exception) throws SAXException {
				printFatalError(exception);
			}

			@Override
			public void error(SAXParseException exception) throws SAXException {
				printError(exception);
			}
		});
		try {
			validator.validate(xmlFile);
		} catch (SAXException e) {
		}
		return output;
	}

	public List<String> correctDTDAgainstXML(File studentenSolutionForDTD) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		DocumentBuilder builder = null;
		
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
		}
		
		builder.setErrorHandler(new ErrorHandler() {
			@Override
			public void warning(SAXParseException exception) throws SAXException {
				String string = null;
				if (exception.getSystemId().indexOf("xml") >= 0) {

					// würde den Fehler in der XML anzeigen
					// nur Ausgabe des Fehlers da die Reihenfolge in der DTD
					// keine Rolle spielt
					string = "WARNING:" + "\n" + "SystemID: " + exception.getSystemId() + "\n" + "Fehler: "
							+ exception.getMessage() + "\n";
					if (!output.contains(string)) {
						output.add(string);
					}

				} else {
					printWarning(exception);
				}

			}

			@Override
			public void fatalError(SAXParseException exception) throws SAXException {
				String string = null;
				if (exception.getSystemId().indexOf("xml") >= 0) {
					// würde den Fehler in der XML anzeigen
					// nur Ausgabe des Fehlers da die Reihenfolge in er DTD
					// keine Rolle spielt
					string = "FATAL ERROR:" + "\n" + "SystemID: " + exception.getSystemId() + "\n" + "Fehler: "
							+ exception.getMessage() + "\n";
					if (!output.contains(string)) {
						output.add(string);
					}

				} else {
					printFatalError(exception);
				}
			}

			@Override
			public void error(SAXParseException exception) throws SAXException {
				String string = null;
				if (exception.getSystemId().indexOf("xml") >= 0) {
					// würde den Fehler in der XML anzeigen
					// nur Ausgabe des Fehlers, da die Reihenfolge in DTD keine
					// Rolle spielt
					string = "ERROR:" + "\n" + "SystemID: " + exception.getSystemId() + "\n" + "Fehler: "
							+ exception.getMessage() + "\n";
					// manche Fehler werden mehrmals ausgegeben, daher
					// überprüfen ob der Fehler schon in der Ausgabe ist

					if (!output.contains(string)) {
						output.add(string);
					}
				} else {
					printError(exception);
				}
			}
		});

		try {
			Document doc = builder.parse(studentenSolutionForDTD);
		} catch (SAXException e) {
		} catch (IOException e) {
		}

		return output;
	}

	private void printWarning(SAXParseException exception) {
		String string = "WARNING:" + "\n" + "SystemID: " + exception.getSystemId() + "\n" + "Zeile: "
				+ exception.getLineNumber() + "\n" + "Fehler" + exception.getMessage() + "\n";
		output.add(string);

	}

	private void printFatalError(SAXParseException exception) {
		String string = "FATAL ERROR:" + "\n" + "SystemID: " + exception.getSystemId() + "\n" + "Zeile: "
				+ exception.getLineNumber() + "\n" + "Fehler: " + exception.getMessage() + "\n";
		output.add(string);
	}

	private void printError(SAXParseException exception) {
		String string = "ERROR:" + "\n" + "SystemID: " + exception.getSystemId() + "\n" + "Zeile: "
				+ exception.getLineNumber() + "\n" + "Fehler: " + exception.getMessage() + "\n";
		output.add(string);
	}
}
