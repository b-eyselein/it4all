package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import com.fasterxml.jackson.databind.JsonNode;
import play.Logger;
import model.UmlDiagramdrawing_Class;
import model.UmlDiagramdrawing_Connection;
import play.libs.Json;

public class UmlDiagramdrawing {
	
	ArrayList<UmlDiagramdrawing_Class> classes_user;
	ArrayList<UmlDiagramdrawing_Class> classes_solution;
	ArrayList<ArrayList<UmlDiagramdrawing_Connection>> connections_user;
	ArrayList<ArrayList<UmlDiagramdrawing_Connection>> connections_solution;
	
	public UmlDiagramdrawing(String input) throws IOException{
		JsonNode node = Json.parse(input);
		//Solution
		JsonNode node_c = Json.parse("{\"classes\":[{\"name\":\"Telekonverter\",\"methods\":[\"\",\"\"],\"attributes\":[\"Verlängerungsfaktor:Zahl\",\"\"]},{\"name\":\"Kameragehäuse\",\"methods\":[\"\",\"\"],\"attributes\":[\"\",\"\"]},{\"name\":\"Profigehäuse\",\"methods\":[\"\",\"\"],\"attributes\":[\"\",\"\"]},{\"name\":\"Profiblitz\",\"methods\":[\"\",\"\"],\"attributes\":[\"\",\"\"]},{\"name\":\"Amateurgehäuse\",\"methods\":[\"\",\"\"],\"attributes\":[\"\",\"\"]},{\"name\":\"Amateurblitz\",\"methods\":[\"\",\"\"],\"attributes\":[\"\",\"\"]},{\"name\":\"Objektiv\",\"methods\":[\"\",\"\"],\"attributes\":[\"Gewindedurchmesser:Zahl\",\"\"]},{\"name\":\"Sonnenblende\",\"methods\":[\"\",\"\"],\"attributes\":[\"Gewindedurchmesser:Zahl\",\"\"]},{\"name\":\"Festbrennweitenobjektiv\",\"methods\":[\"\",\"\"],\"attributes\":[\"Brennweite:Zahl\",\"\"]},{\"name\":\"Zoomobjektiv\",\"methods\":[\"\",\"\"],\"attributes\":[\"Brennweite_maximal:Zahl\",\"Brennweite_minimal:Zahl\"]}],\"connections\":{\"standard\":[{\"start\":\"Amateurgehäuse\",\"target\":\"Amateurblitz\",\"mulstart\":\"1\",\"multarget\":\"1\"},{\"start\":\"Objektiv\",\"target\":\"Kameragehäuse\",\"mulstart\":\"1\",\"multarget\":\"1\"},{\"start\":\"Objektiv\",\"target\":\"Sonnenblende\",\"mulstart\":\"1\",\"multarget\":\"1\"},{\"start\":\"Profiblitz\",\"target\":\"Profigehäuse\",\"mulstart\":\"1\",\"multarget\":\"1\"},{\"start\":\"Telekonverter\",\"target\":\"Kameragehäuse\",\"mulstart\":\"1\",\"multarget\":\"1\"}],\"aggregation\":[],\"composition\":[],\"implementation\":[],\"generalization\":[{\"start\":\"Amateurgehäuse\",\"target\":\"Amateurblitz\",\"mulstart\":\"1\",\"multarget\":\"1\"},{\"start\":\"Objektiv\",\"target\":\"Kameragehäuse\",\"mulstart\":\"1\",\"multarget\":\"1\"},{\"start\":\"Objektiv\",\"target\":\"Sonnenblende\",\"mulstart\":\"1\",\"multarget\":\"1\"},{\"start\":\"Profiblitz\",\"target\":\"Profigehäuse\",\"mulstart\":\"1\",\"multarget\":\"1\"}]}}");
		this.classes_user = convertClassesToObject(node);
		this.classes_solution = convertClassesToObject(node_c);
		this.connections_user = convertConnectionsToObject(node);
		this.connections_user = convertConnectionsToObject(node_c);
	}
	
	public ArrayList<UmlDiagramdrawing_Class> convertClassesToObject(JsonNode mainNode){
		ArrayList<UmlDiagramdrawing_Class> classes = new ArrayList<>();
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
		    	classes.add(new UmlDiagramdrawing_Class(name,al_methods, al_attributes));
		    }
		}
		return classes;
	}
			
	public ArrayList<ArrayList<UmlDiagramdrawing_Connection>> convertConnectionsToObject(JsonNode mainNode){
		ArrayList<ArrayList<UmlDiagramdrawing_Connection>> connections = new ArrayList<>();
		JsonNode node_connectionType = mainNode.get("connections");
		String[] types = {"standard","aggregation","composition","implementation","generalization"};
		for (int i = 0; i < types.length; i++) {
			connections.add(convertConnectionypeForConnections(node_connectionType.get(types[i]),types[i]));
		}
		Logger.debug("connectionssize"+connections.size());
		return connections;
	}
	
	public ArrayList<UmlDiagramdrawing_Connection> convertConnectionypeForConnections(JsonNode type, String str_type){
		ArrayList<UmlDiagramdrawing_Connection> connections = new ArrayList<>();
		for(final JsonNode objNode : type){
			String kind = str_type;
			String start= objNode.get("start").toString();
			String target= objNode.get("target").toString();
			String mulstart= objNode.get("mulstart").toString();
			String multarget= objNode.get("multarget").toString();			
			connections.add(new UmlDiagramdrawing_Connection(kind, start, target, mulstart, multarget));
			//Logger.debug("connection "+kind+" start: "+start+" target: "+target+" mulstart: "+mulstart+" multarget: "+multarget);
		}
		return connections;
	}
	
	
	

}

