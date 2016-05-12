package model;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import model.ElementResult;
import model.user.User;

public class XmlCorrector {

	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;

	public XmlCorrector() {
		factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

	}

	private String dtdToXml(File file1, File file2) throws FileNotFoundException, IOException {
		FileInputStream fstream1 = new FileInputStream(file1);
		FileInputStream fstream2 = new FileInputStream(file2);

		DataInputStream in1 = new DataInputStream(fstream1);
		DataInputStream in2 = new DataInputStream(fstream2);

		BufferedReader br1 = new BufferedReader(new InputStreamReader(in1));
		BufferedReader br2 = new BufferedReader(new InputStreamReader(in2));

		String strLine1 = null, strLine2 = null;
		List<String> output = new ArrayList<>();
		List<String> output2 = new ArrayList<>();
		int lines = 0;

		while ((strLine1 = br1.readLine()) != null && (strLine2 = br2.readLine()) != null) {
			lines++;
			if (!strLine1.equals(strLine2)) {
				output.add(strLine1 + "\n" + "Zeile: " + lines);
				output2.add(strLine2);
			}
		}

		String result = new String();
		if (!output.isEmpty()) {
			for (String string : output) {
				result.concat("But was: " + string + "\n");
			}
		} else {
			for (String string : output) {
				result.concat(string);
			}
		}

		return result;
	}

	private String xmlToDTD(File file) {
		final String output = new String();
		builder.setErrorHandler(new ErrorHandler() {
			@Override
			public void warning(SAXParseException exception) throws SAXException {
				String string = "WARNING:" + "\n" + "SystemID: " + exception.getSystemId() + "\n" + "Zeile: "
						+ exception.getLineNumber() + "\n" + "Spalte: " + exception.getColumnNumber() + "\n" + "Fehler"
						+ exception.getMessage() + "\n";
				output.concat(string);
			}

			@Override
			public void fatalError(SAXParseException exception) throws SAXException {
				String string = "WARNING:" + "\n" + "SystemID: " + exception.getSystemId() + "\n" + "Zeile: "
						+ exception.getLineNumber() + "\n" + "Spalte: " + exception.getColumnNumber() + "\n" + "Fehler"
						+ exception.getMessage() + "\n";
				output.concat(string);
			}

			@Override
			public void error(SAXParseException exception) throws SAXException {
				String string = "WARNING:" + "\n" + "SystemID: " + exception.getSystemId() + "\n" + "Zeile: "
						+ exception.getLineNumber() + "\n" + "Spalte: " + exception.getColumnNumber() + "\n" + "Fehler"
						+ exception.getMessage() + "\n";
				output.concat(string);
			}
		});
		try {
			Document doc = builder.parse(file);
		} catch (SAXException e) {
		} catch (IOException e) {
		}
		return output;
	}

	private String xmlToXSD(File xsd, File xml) throws IOException {
		final String output = new String();
		Source schemaFile = new StreamSource(xsd);

		Source xmlFile = new StreamSource(xml);
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
				output.concat(string);
			}

			@Override
			public void fatalError(SAXParseException exception) throws SAXException {
				String string = "FATAL ERROR:" + "\n" + "SystemID: " + exception.getSystemId() + "\n" + "Zeile: "
						+ exception.getLineNumber() + "\n" + "Spalte: " + exception.getColumnNumber() + "\n"
						+ "Fehler: " + exception.getMessage() + "\n";
				output.concat(string);
			}

			@Override
			public void error(SAXParseException exception) throws SAXException {
				String string = "ERROR:" + "\n" + "SystemID: " + exception.getSystemId() + "\n" + "Zeile: "
						+ exception.getLineNumber() + "\n" + "Spalte: " + exception.getColumnNumber() + "\n" + "Fehler"
						+ exception.getMessage() + "\n";
				output.concat(string);
			}
		});
		try {
			validator.validate(xmlFile);
		} catch (SAXException e) {
		}

		return !output.isEmpty() ? output : "Keine Fehler";
	}

	public static List<ElementResult> correct(String solutionUrl, XmlExercise exercise, User student) {
	    // TODO Do some correction depending on type of exercise
	    
	    List<ElementResult> result = new ArrayList<ElementResult>();
	    
	    return result;
	  }

}
