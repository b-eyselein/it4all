package model;

import com.fasterxml.jackson.databind.JsonNode;

import play.Logger;
import play.libs.Json;


public class UmlDiagramdrawing {
	
	public UmlDiagramdrawing(String input){
		JsonNode node = Json.parse(input);
		JsonNode node_c = Json.parse("{\"classes\":[{\"name\":\"Telekonverter\",\"methods\":[\"\",\"\"],\"attributes\":[\"Verlängerungsfaktor:Zahl\",\"\"]},{\"name\":\"Kameragehäuse\",\"methods\":[\"\",\"\"],\"attributes\":[\"\",\"\"]},{\"name\":\"Profigehäuse\",\"methods\":[\"\",\"\"],\"attributes\":[\"\",\"\"]},{\"name\":\"Profiblitz\",\"methods\":[\"\",\"\"],\"attributes\":[\"\",\"\"]},{\"name\":\"Amateurgehäuse\",\"methods\":[\"\",\"\"],\"attributes\":[\"\",\"\"]},{\"name\":\"Amateurblitz\",\"methods\":[\"\",\"\"],\"attributes\":[\"\",\"\"]},{\"name\":\"Objektiv\",\"methods\":[\"\",\"\"],\"attributes\":[\"Gewindedurchmesser:Zahl\",\"\"]},{\"name\":\"Sonnenblende\",\"methods\":[\"\",\"\"],\"attributes\":[\"Gewindedurchmesser:Zahl\",\"\"]},{\"name\":\"Festbrennweitenobjektiv\",\"methods\":[\"\",\"\"],\"attributes\":[\"Brennweite:Zahl\",\"\"]},{\"name\":\"Zoomobjektiv\",\"methods\":[\"\",\"\"],\"attributes\":[\"Brennweite_maximal:Zahl\",\"Brennweite_minimal:Zahl\"]}],\"connections\":{\"standard\":[{\"start\":\"Amateurgehäuse\",\"target\":\"Amateurblitz\",\"mulstart\":\"1\",\"multarget\":\"1\"},{\"start\":\"Objektiv\",\"target\":\"Kameragehäuse\",\"mulstart\":\"1\",\"multarget\":\"1\"},{\"start\":\"Objektiv\",\"target\":\"Sonnenblende\",\"mulstart\":\"1\",\"multarget\":\"1\"},{\"start\":\"Profiblitz\",\"target\":\"Profigehäuse\",\"mulstart\":\"1\",\"multarget\":\"1\"},{\"start\":\"Telekonverter\",\"target\":\"Kameragehäuse\",\"mulstart\":\"1\",\"multarget\":\"1\"}],\"aggregation\":[],\"composition\":[],\"implementation\":[],\"generalization\":[{\"start\":\"Amateurgehäuse\",\"target\":\"Amateurblitz\",\"mulstart\":\"1\",\"multarget\":\"1\"},{\"start\":\"Objektiv\",\"target\":\"Kameragehäuse\",\"mulstart\":\"1\",\"multarget\":\"1\"},{\"start\":\"Objektiv\",\"target\":\"Sonnenblende\",\"mulstart\":\"1\",\"multarget\":\"1\"},{\"start\":\"Profiblitz\",\"target\":\"Profigehäuse\",\"mulstart\":\"1\",\"multarget\":\"1\"}]}}");
		//Klassen
		Logger.debug("getclasses: " + Json.prettyPrint(node.get("classes")));
		Logger.debug("getconnections: " + Json.prettyPrint(node.get("connections")));
		
		
		//Verbindungen
	
	
	}
}
