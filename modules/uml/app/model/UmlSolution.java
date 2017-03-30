package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import model.UmlExercise;
import play.libs.Json;

public class UmlSolution {
	
	private List<String> classes;
	private List<String> methods;
	private List<String> attributes;
	private static final Path BASE_PATH = Paths.get("modules", "uml", "conf", "resources");
	private static final Path MUSTER_SOLUTION = Paths.get(BASE_PATH.toString(), "mustersolution_classSel.json");
	
public UmlSolution(){
    String musterSolution = "";
    try {
      musterSolution = String.join("\n", Files.readAllLines(MUSTER_SOLUTION));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    JsonNode solutionJSON = Json.parse(musterSolution);
    List<String> classes = parseJSONArray(solutionJSON.get("classes"));
    List<String> methods = parseJSONArray(solutionJSON.get("methods"));
    List<String> attributes = parseJSONArray(solutionJSON.get("attributes"));
    this.classes=classes;
    this.methods=methods;
    this.attributes=attributes;
}

//doppelt...
protected static List<String> parseJSONArray(JsonNode jsonArrayNode) {
    List<String> ret = new LinkedList<>();
    for(JsonNode jsonNode: jsonArrayNode)
      ret.add(jsonNode.asText());
    return ret;
  }

public List<String> getClasses(){
	return this.classes;
}

public List<String> getMethods(){
	return this.methods;
}

public List<String> getAttributes(){
	return this.attributes;
}
	
public String getEntryClasses(int index){
	return this.classes.get(index);
}
public String getEntryMethods(int index){
	return this.methods.get(index);
}
public String getEntryAttributes(int index){
	return this.attributes.get(index);
}




}
