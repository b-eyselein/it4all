package model.xml;

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

public class XMLagainstDTDandXSD {
	public void XMLAgainstDTD(File studentensolutionForXML) {
		
		final List<String> output = new LinkedList<>();

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
				String string = "WARNING:" + "\n" + "SystemID: " + exception.getSystemId() + "\n" + "Zeile: "
						+ exception.getLineNumber() + "\n" + "Fehler" + exception.getMessage() + "\n";
				output.add(string);
			}

			@Override
			public void fatalError(SAXParseException exception) throws SAXException {
				String string = "FATAL ERROR:" + "\n" + "SystemID: " + exception.getSystemId() + "\n" + "Zeile: "
						+ exception.getLineNumber() + "\n" + "Fehler: " + exception.getMessage() + "\n";
				output.add(string);

			}

			@Override
			public void error(SAXParseException exception) throws SAXException {
				String string = "ERROR:" + "\n" + "SystemID: " + exception.getSystemId() + "\n" + "Zeile: "
						+ exception.getLineNumber() + "\n" + "Fehler: " + exception.getMessage() + "\n";
				output.add(string);

			}
		});

		try {
			Document doc = builder.parse(studentensolutionForXML);

		} catch (SAXException e) {
		} catch (IOException e) {
		}
		for (String item : output) {
			System.out.println(item);
		}
	}

	public void XMLAgainstXSD(File studentSolution, File sampleSolution) throws IOException {
		final List<String> output = new LinkedList<>();
		Source schemaFile = new StreamSource(studentSolution);

		Source xmlFile = new StreamSource(sampleSolution);
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
						+ exception.getLineNumber() + "\n" + "Fehler: " + exception.getMessage() + "\n";
				output.add(string);
			}

			@Override
			public void fatalError(SAXParseException exception) throws SAXException {
				String string = "FATAL ERROR:" + "\n" + "SystemID: " + exception.getSystemId() + "\n" + "Zeile: "
						+ exception.getLineNumber() + "\n" + "Fehler: " + exception.getMessage() + "\n";

				output.add(string);
			}

			@Override
			public void error(SAXParseException exception) throws SAXException {
				String string = "ERROR:" + "\n" + "SystemID: " + exception.getSystemId() + "\n" + "Zeile: "
						+ exception.getLineNumber() + "\n" + "Fehler: " + exception.getMessage() + "\n" + "\n"
						+ exception.getMessage();
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
			System.out.println("Dokument ist fehlerfrei");
		}
	}

	public void DTDAgainstXML(File studentenSolutionForDTD) {
		final List<String> output = new LinkedList<>();

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
					
					//würde den Fehler in der XML anzeigen
					//nur Ausgabe des Fehlers da die Reihenfolge in er DTD keine Rolle spielt
					string = "WARNING:" + "\n" + "SystemID: " + exception.getSystemId() + "\n" + "Fehler: "
							+ exception.getMessage() + "\n";
				} else {
					string = "WARNING:" + "\n" + "SystemID: " + exception.getSystemId() + +exception.getLineNumber()
							+ "\n" + "Fehler" + exception.getMessage() + "\n";
				}

				output.add(string);

			}

			@Override
			public void fatalError(SAXParseException exception) throws SAXException {
				String string = null;
				if (exception.getSystemId().indexOf("xml") >= 0) {
					//würde den Fehler in der XML anzeigen
					//nur Ausgabe des Fehlers da die Reihenfolge in er DTD keine Rolle spielt
					string = "FATAL ERROR:" + "\n" + "SystemID: " + exception.getSystemId() + "\n" + "Fehler: "
							+ exception.getMessage() + "\n";
				} else {
					string = "FATAL ERROR:" + "\n" + "SystemID: " + exception.getSystemId() + +exception.getLineNumber()
							+ "\n" + "Fehler" + exception.getMessage() + "\n";
				}

				output.add(string);

			}
			@Override
			public void error(SAXParseException exception) throws SAXException {
				String string = null;
				if (exception.getSystemId().indexOf("xml") >= 0) {
					//würde den Fehler in der XML anzeigen
					//nur Ausgabe des Fehlers, da die Reihenfolge in DTD keine Rolle spielt
					string = "ERROR:" + "\n" + "SystemID: " + exception.getSystemId() + "\n" + "Fehler: "
							+ exception.getMessage() + "\n";
				} else {
					string = "ERROR:" + "\n" + "SystemID: " + exception.getSystemId() + +exception.getLineNumber()
							+ "\n" + "Fehler" + exception.getMessage() + "\n";
				}

				if (!output.contains(string)) {
					output.add(string);
				}
			}
		});

		try {
			Document doc = builder.parse(studentenSolutionForDTD);

		} catch (SAXException e) {
		} catch (IOException e) {
		}
		for (String item : output) {
			System.out.println(item);
		}
	}
	
//	private void printWarning(SAXParseException exception){
//		
//	}

	public static void main(String[] args) throws IOException {
//		XMLagainstDTDandXSD xml = new XMLagainstDTDandXSD();
//		 xml.XMLAgainstDTD(new File("//home//shpend//Downloads//party.xml"));
		// xml.XMLAgainstXSD(new File("//home/shpend//Downloads//books.xsd"),
		// new File("//home/shpend//Downloads//xmlFile.xml"));
		// xml.DTDAgainstXML(new File("//home//shpend//Downloads//party.xml"));

	}

}
