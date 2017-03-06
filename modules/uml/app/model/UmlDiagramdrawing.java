package model;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import model.UmlClassselection_Class;
import play.Logger;
import play.libs.Json;



public class UmlDiagramdrawing {
	
	ArrayList<UmlClassselection_Class> classes_user;
	ArrayList<UmlClassselection_Class> classes_solution;
	

	//jsonpatch or jsondiff, falls ohne Aehnlichkeitsvergleich
	public UmlDiagramdrawing(String input) throws IOException{
		JsonNode node = Json.parse(input);
		JsonNode node_c = Json.parse("{\"classes\":[{\"name\":\"Telekonverter\",\"methods\":[\"\",\"\"],\"attributes\":[\"Verlängerungsfaktor:Zahl\",\"\"]},{\"name\":\"Kameragehäuse\",\"methods\":[\"\",\"\"],\"attributes\":[\"\",\"\"]},{\"name\":\"Profigehäuse\",\"methods\":[\"\",\"\"],\"attributes\":[\"\",\"\"]},{\"name\":\"Profiblitz\",\"methods\":[\"\",\"\"],\"attributes\":[\"\",\"\"]},{\"name\":\"Amateurgehäuse\",\"methods\":[\"\",\"\"],\"attributes\":[\"\",\"\"]},{\"name\":\"Amateurblitz\",\"methods\":[\"\",\"\"],\"attributes\":[\"\",\"\"]},{\"name\":\"Objektiv\",\"methods\":[\"\",\"\"],\"attributes\":[\"Gewindedurchmesser:Zahl\",\"\"]},{\"name\":\"Sonnenblende\",\"methods\":[\"\",\"\"],\"attributes\":[\"Gewindedurchmesser:Zahl\",\"\"]},{\"name\":\"Festbrennweitenobjektiv\",\"methods\":[\"\",\"\"],\"attributes\":[\"Brennweite:Zahl\",\"\"]},{\"name\":\"Zoomobjektiv\",\"methods\":[\"\",\"\"],\"attributes\":[\"Brennweite_maximal:Zahl\",\"Brennweite_minimal:Zahl\"]}],\"connections\":{\"standard\":[{\"start\":\"Amateurgehäuse\",\"target\":\"Amateurblitz\",\"mulstart\":\"1\",\"multarget\":\"1\"},{\"start\":\"Objektiv\",\"target\":\"Kameragehäuse\",\"mulstart\":\"1\",\"multarget\":\"1\"},{\"start\":\"Objektiv\",\"target\":\"Sonnenblende\",\"mulstart\":\"1\",\"multarget\":\"1\"},{\"start\":\"Profiblitz\",\"target\":\"Profigehäuse\",\"mulstart\":\"1\",\"multarget\":\"1\"},{\"start\":\"Telekonverter\",\"target\":\"Kameragehäuse\",\"mulstart\":\"1\",\"multarget\":\"1\"}],\"aggregation\":[],\"composition\":[],\"implementation\":[],\"generalization\":[{\"start\":\"Amateurgehäuse\",\"target\":\"Amateurblitz\",\"mulstart\":\"1\",\"multarget\":\"1\"},{\"start\":\"Objektiv\",\"target\":\"Kameragehäuse\",\"mulstart\":\"1\",\"multarget\":\"1\"},{\"start\":\"Objektiv\",\"target\":\"Sonnenblende\",\"mulstart\":\"1\",\"multarget\":\"1\"},{\"start\":\"Profiblitz\",\"target\":\"Profigehäuse\",\"mulstart\":\"1\",\"multarget\":\"1\"}]}}");
		//Klassen
		this.classes_user = convertClassesToObject(node);
		this.classes_solution = convertClassesToObject(node_c);
		
		
		
		
		
		
		
		/*
		JsonNode node_classes_c = node_c.get("classes");
		ArrayList<UmlClassselection_Class> classes = new ArrayList<>();
		JsonNode node_classes = node.get("classes");
		if (node_classes.isArray()) {
		    for (final JsonNode objNode : node_classes) {
		    	String name = objNode.get("name").toString();
		    	String[] str_methods = objNode.get("methods").toString().substring(1, objNode.get("methods").toString().length() - 1)
		    	        .split(",");
		    	ArrayList<String> al_methods = new ArrayList<String>(Arrays.asList(str_methods));
		    	
		    	String[] str_attributes = objNode.get("attributes").toString().substring(1, objNode.get("attributes").toString().length() - 1)
		    	        .split(",");
		    	ArrayList<String> al_attributes = new ArrayList<String>(Arrays.asList(str_attributes));
		    	java.util.Collections.sort(al_attributes);
		    	java.util.Collections.sort(al_methods);
		    	classes.add(new UmlClassselection_Class(name,al_methods, al_attributes));
		    }
		}
		Logger.debug("size "+classes.size());
		*/
	}
	
	public ArrayList<UmlClassselection_Class> convertClassesToObject(JsonNode mainNode){
		ArrayList<UmlClassselection_Class> classes = new ArrayList<>();
		JsonNode node_classes = mainNode.get("classes");
		if (node_classes.isArray()) {
		    for (final JsonNode objNode : node_classes) {
		    	String name = objNode.get("name").toString();
		    	String[] str_methods = objNode.get("methods").toString().substring(1, objNode.get("methods").toString().length() - 1)
		    	        .split(",");
		    	ArrayList<String> al_methods = new ArrayList<String>(Arrays.asList(str_methods));
		    	
		    	String[] str_attributes = objNode.get("attributes").toString().substring(1, objNode.get("attributes").toString().length() - 1)
		    	        .split(",");
		    	ArrayList<String> al_attributes = new ArrayList<String>(Arrays.asList(str_attributes));
		    	java.util.Collections.sort(al_attributes);
		    	java.util.Collections.sort(al_methods);
		    	classes.add(new UmlClassselection_Class(name,al_methods, al_attributes));
		    }
		}
		return classes;
	}
	
}
		
		
/*
		while(it.hasNext()){
			JsonNode node_it = it.next();
			//Logger.debug(Json.prettyPrint(node_it));
			String name = node_it.get("name").textValue();
			//Logger.debug("node: " + Json.prettyPrint(node_it));
			//Logger.debug("getclasses: " + Json.prettyPrint(node_it.get("classes")));
			//Logger.debug("getmethods: " + Json.prettyPrint(node_it.get("methods")));
			String[] methods = node_it.get("methods").toString().substring(1, node_it.get("methods").toString().length() - 1).split(",");
			String[] attributes = node_it.get("attributes").toString().substring(1, node_it.get("attributes").toString().length() - 1).split(",");
		    ArrayList<String> al_methods = new ArrayList<String>(Arrays.asList(methods));
		    ArrayList<String> al_attributes = new ArrayList<String>(Arrays.asList(attributes));
		    while(it_c.hasNext()){
		    	JsonNode node_it_c = it_c.next();
		    	String name_c = node_it_c.get("name").textValue();
		    	//Überprüfe nur Klassennamen 
		    	if(name.equals(name_c)){
		    		Logger.debug("if: "+name);
		    		ArrayList<String> arraylist = new ArrayList<>();		    		
		    		arraylist.add(name);
		    		classes.add(arraylist);		    		
		    		String[] methods_c = node_it_c.get("methods").toString().substring(1, node_it_c.get("methods").toString().length() - 1).split(",");
					String[] attributes_c = node_it_c.get("attributes").toString().substring(1, node_it_c.get("attributes").toString().length() - 1).split(",");
				    ArrayList<String> al_methods_c = new ArrayList<String>(Arrays.asList(methods_c));
				    ArrayList<String> al_attributes_c = new ArrayList<String>(Arrays.asList(attributes_c));
				    ArrayList<String> methods_uc = new ArrayList<>();
				    for(String object: al_methods) {
				        if(al_methods_c.contains(object)) {
				        	methods_uc.add(object);
				        	al_methods.remove(object);
				        	// mit dem remove werden alle falschen methoden in al_methods zu jeder klasse
				        }
				    }
				    classes.
				    ArrayList<String> attributes_uc = new ArrayList<>();
				    for(String object: al_attributes) {
				        if(al_attributes_c.contains(object)) {
				        	attributes_uc.add(object);
				        	al_attributes.remove(object);
				        	// mit dem remove werden alle falschen attr in al_attr zu der klasse
				        }
				    }
				    
				    
				    
		    	}else{
		    		Logger.debug("klasse falsch: "+name_c);
		    	}
		    }
		    
  */
			/*
			for (int i = 0; i < attributes.length; i++) {
				Logger.debug("attributes "+attributes[i]);
			}
			for (int i = 0; i < methods.length; i++) {
				Logger.debug("methods "+methods[i]+ "<--");
			}
			*/
			//Logger.debug(methods.toString());
			//Logger.debug(attributes.toString());
			
			

		
		
		
		
		
		
		//Logger.debug("getclasses: " + Json.prettyPrint(node.get("classes")));
		//Logger.debug("getconnections: " + Json.prettyPrint(node.get("connections")));


		//Verbindungen

	
	
