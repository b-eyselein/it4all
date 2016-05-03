package controllers.xml;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XmlAgainstDTD {
	public static void main(String[] args)  {
		final List<String> output = new LinkedList<>();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true) ;
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		builder.setErrorHandler(new ErrorHandler() {
			
			@Override
			public void warning(SAXParseException exception) throws SAXException {
				String string = "WARNING:" + "\n" + "SystemID: " + exception.getSystemId() + "\n" + "Zeile: "
						+ exception.getLineNumber() + "\n" + "Spalte: " + exception.getColumnNumber() + "\n" + "Fehler"
						+ exception.getMessage() + "\n";
				output.add(string);
			}
			
			@Override
			public void fatalError(SAXParseException exception) throws SAXException {
				String string = "WARNING:" + "\n" + "SystemID: " + exception.getSystemId() + "\n" + "Zeile: "
						+ exception.getLineNumber() + "\n" + "Spalte: " + exception.getColumnNumber() + "\n" + "Fehler"
						+ exception.getMessage() + "\n";
				output.add(string);
				
			}
			
			@Override
			public void error(SAXParseException exception) throws SAXException {
				String string = "WARNING:" + "\n" + "SystemID: " + exception.getSystemId() + "\n" + "Zeile: "
						+ exception.getLineNumber() + "\n" + "Spalte: " + exception.getColumnNumber() + "\n" + "Fehler"
						+ exception.getMessage() + "\n";
				output.add(string);
				
			}
		});
		
		try {
			Document doc = builder.parse(new File("//home//shpend//Downloads//party.xml"));
		} catch (SAXException e) {
		} catch (IOException e) {
		}
		for (String item : output) {
			System.out.println(item);
		}
		
	}
}
