package model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

import model.user.User;

public class XmlCorrector {
	public static List<XMLError> correctXMLAgainstDTD(File studentensolutionForXML) {
		List<XMLError> output = new LinkedList<>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
		}
		builder.setErrorHandler(new ErrorHandler() {
			//kommt in eigene Klasse

			@Override
			public void warning(SAXParseException exception) throws SAXException {
				output.add(printWarning(exception));
			}

			@Override
			public void fatalError(SAXParseException exception) throws SAXException {
				output.add(printFatalError(exception));
			}

			@Override
			public void error(SAXParseException exception) throws SAXException {
				output.add(printError(exception));

			}
		});

		try {
			Document doc = builder.parse(studentensolutionForXML);

		} catch (SAXException e) {
		} catch (IOException e) {
		}

		return output;
	}

	public static List<XMLError> correctXMLAgainstXSD(File studentSolution, File sampleSolution) throws IOException {
		List<XMLError> output = new LinkedList<>();
		Source schemaFile = new StreamSource(sampleSolution);
		Source xmlFile = new StreamSource(studentSolution);
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
				output.add(printWarning(exception));
			}

			@Override
			public void fatalError(SAXParseException exception) throws SAXException {
				output.add(printFatalError(exception));
			}

			@Override
			public void error(SAXParseException exception) throws SAXException {
				output.add(printError(exception));

			}
		});
		try {
			validator.validate(xmlFile);
		} catch (SAXException e) {
		}
		return output;
	}

	public static List<XMLError> correctDTDAgainstXML(File studentenSolutionForDTD) {
		List<XMLError> output = new LinkedList<>();
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
					// nur Ausgabe des Fehlers da die Reihenfolge in er DTD
					// keine Rolle spielt
					string = "WARNING:" + "\n" + "Fehler: "
							+ exception.getMessage() + "\n";
					//TODO
//					if (!output.contains(string)) {
//						output.add(string);
//					}

				} else {
					output.add(printWarning(exception));
				}

			}

			@Override
			public void fatalError(SAXParseException exception) throws SAXException {
				String string = null;
				if (exception.getSystemId().indexOf("xml") >= 0) {
					// würde den Fehler in der XML anzeigen
					// nur Ausgabe des Fehlers da die Reihenfolge in er DTD
					// keine Rolle spielt
					string = "FATAL ERROR:" + "\n" + "Fehler: "
							+ exception.getMessage() + "\n";
					//TODO
//					if (!output.contains(string)) {
//						output.add(string);
//					}

				} else {
					output.add(printFatalError(exception));
				}
			}

			@Override
			public void error(SAXParseException exception) throws SAXException {
				String string = null;
				if (exception.getSystemId().indexOf("xml") >= 0) {
					// würde den Fehler in der XML anzeigen
					// nur Ausgabe des Fehlers, da die Reihenfolge in DTD keine
					// Rolle spielt
					string = "ERROR:" + "\n" + "Fehler: "
							+ exception.getMessage() + "\n";
					// manche Fehler werden mehrmals ausgegeben, daher
					// überprüfen ob der Fehler schon in der Ausgabe ist
					//TODO
//					if (!output.contains(string)) {
//						ElementResult result = new ElementResult(Success.PARTIALLY, "", string);
//						output.add(result);
//					}
				} else {
					output.add(printError(exception));
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

	private static XMLError printWarning(SAXParseException exception) {
		XMLError error = new XMLError(exception.getLineNumber(), exception.getMessage(), XmlErrorType.WARNING);
		return error;
	}

	private static XMLError printFatalError(SAXParseException exception) {
		XMLError error = new XMLError(exception.getLineNumber(), exception.getMessage(), XmlErrorType.FATALERROR);
		return error;
	}

	private static XMLError printError(SAXParseException exception) {
		XMLError error = new XMLError(exception.getLineNumber(), exception.getMessage(), XmlErrorType.ERROR);
		return error;
	}

	public static List<XMLError> correct(File solutionFile, File referenceFile, XmlExercise exercise, User user) {
		if (exercise.exerciseType == 0) {
			try {
				return correctXMLAgainstXSD(solutionFile, referenceFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (exercise.exerciseType == 1) {
			return correctXMLAgainstDTD(solutionFile);
		}
		else if (exercise.exerciseType == 2) {
			// TODO
			return correctXMLAgainstDTD(solutionFile);
		}
		if (exercise.exerciseType == 3) {
			return correctDTDAgainstXML(solutionFile);
		}
		return null;
	}
}
