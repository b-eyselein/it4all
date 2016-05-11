package model.xml;

import java.util.List;
import java.util.stream.Collectors;

import model.html.result.ElementResult;
import model.html.task.Task;
import model.user.Student;
import model.user.User;

public class XmlCorrector {

  private DocumentBuilderFactory factory;
  private DocumentBuilder builder;

  public XmlCorrector(){
    factory = DocumentBuilderFactory.newInstance()
	  factory.setValidating(true);
    builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
      //	e.printStackTrace();
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
  }

  private String dtdToXml(File file1, File file2){
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

    String result;
    if (!output.isEmpty()) {
      result = new String();
      for (String string : output) {
        result.add("But was: " + string + "\n");
      }
    }
    else{
      result = new String(output);
    }

    return result;
  }

  private String xmlToDTD(File file){
    final List<String> output = new LinkedList<>();

				try {
			Document doc = builder.parse(file);
		} catch (SAXException e) {
		} catch (IOException e) {
		}
    return output;
  }

  private String xmlToXSD(File xsd, File xml){
    final List<String> output = new LinkedList<>();
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

    return !output.isEmpty() ? output : "Keine Fehler";
	}
  }

  // TODO: change List<Object> to something custom
  public static List<Object> correct(String solutionText, XmlExercise exercise, User student) {
    // the path of the reference file could be
	// found by XmlExercise.pathToReference
	// TODO!!!

    return result;
  }

}
