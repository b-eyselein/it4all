package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import com.fasterxml.jackson.databind.JsonNode;
import play.Logger;
import play.libs.Json;

public class UmlClassselection {

  private String title;	
  private String json ="solutionComments";
  private ArrayList<String>  classes_c;
  private ArrayList<String>  classes_f;
  private ArrayList<String>  classes_m;
  private ArrayList<String>  methods_c;
  private ArrayList<String>  methods_f;
  private ArrayList<String>  methods_m;
  private ArrayList<String>  attributes_c;
  private ArrayList<String>  attributes_f;
  private ArrayList<String>  attributes_m;


  
  public UmlClassselection(String input) {
	setTitleExcercise("Foto");
    JsonNode node = Json.parse(input);
    node.get("classes");
    JsonNode node_c = Json.parse(
        "{\"classes\":[\"Telekonverter\",\"Profigehäuse\",\"Kameragehäuse\",\"Amateurgehäuse\",\"Profiblitz\",\"Objektiv\",\"Amateurblitz\",\"Sonnenblende\",\"Zoomobjektiv\",\"Festweitenobjektiv\"],\"methods\":[\"Hersteller\",\"Fotosystems\"],\"attributes\":[\"Ikonograf\",\"Webseite\"]}");
   // Logger.debug(Json.prettyPrint(node));
    //Logger.debug("getclasses: " + Json.prettyPrint(node.get("classes")));
    // Logger.debug("getmethods: " + Json.prettyPrint(node.get("methods")));
    //Logger.debug("getattr: " + Json.prettyPrint(node.get("attributes")));
    // Init
    String[] arr_classes = node.get("classes").toString().substring(1, node.get("classes").toString().length() - 1)
        .split(",");
    String[] arr_methods = node.get("methods").toString().substring(1, node.get("methods").toString().length() - 1)
        .split(",");
    String[] arr_attributes = node.get("attributes").toString()
        .substring(1, node.get("attributes").toString().length() - 1).split(",");
    String[] arr_classes_c = node_c.get("classes").toString()
        .substring(1, node_c.get("classes").toString().length() - 1).split(",");
    String[] arr_methods_c = node_c.get("methods").toString()
        .substring(1, node_c.get("methods").toString().length() - 1).split(",");
    String[] arr_attributes_c = node_c.get("attributes").toString()
        .substring(1, node_c.get("attributes").toString().length() - 1).split(",");

    //String json = "{\"correct\":[{\"classes\":[";
    // correct
    ArrayList<String> al_classes = new ArrayList<String>(Arrays.asList(arr_classes));
    ArrayList<String> al_classes_c = new ArrayList<String>(Arrays.asList(arr_classes_c));
    ArrayList<String> classes_c= new ArrayList<>();
    for(String object: al_classes) {
      if(al_classes_c.contains(object)) {
    	  //Logger.debug("Klasse: " + object + " stimmt.");
        classes_c.add(object);
        // json += object + ",";
      }
    }
    this.classes_c=classes_c;
    //if(json.endsWith(",")) {
    //  json = json.substring(0, json.length() - 1);
    //}
    //json += "],\"methods\":[";
    ArrayList<String> al_methods = new ArrayList<String>(Arrays.asList(arr_methods));
    ArrayList<String> al_methods_c = new ArrayList<String>(Arrays.asList(arr_methods_c));
    ArrayList<String> methods_c= new ArrayList<>();
    for(String object: al_methods) {
      if(al_methods_c.contains(object)) {
    	  //Logger.debug("Methode: " + object + " stimmt.");
        methods_c.add(object);
        //json += object + ",";
      }
    }
    this.methods_c=methods_c;
    //if(json.endsWith(",")) {
    // json = json.substring(0, json.length() - 1);
      //}
   // json += "],\"attributes\":[";
    ArrayList<String> al_attributes = new ArrayList<String>(Arrays.asList(arr_attributes));
    ArrayList<String> al_attributes_c = new ArrayList<String>(Arrays.asList(arr_attributes_c));
    ArrayList<String> attributes_c= new ArrayList<>();
    for(String object: al_attributes) {
      if(al_attributes_c.contains(object)) {
    	  // Logger.debug("Attribut: " + object + " stimmt.");
        attributes_c.add(object);
        // json += object + ",";
      }
    }
    this.attributes_c=attributes_c;
    //if(json.endsWith(",")) {
    //   json = json.substring(0, json.length() - 1);
    // }
    // false
    // json += "]}],\"false\":[{\"classes\":[";   
    ArrayList<String> classes_f= new ArrayList<>();
    for(String object: al_classes) {
      if(!al_classes_c.contains(object)){
          json += object + ",";
          classes_f.add(object);
      }
    this.classes_f=classes_f;
    }
    //if(json.endsWith(",")) {
    //  json = json.substring(0, json.length() - 1);
    // }
    // json += "],\"methods\":[";
    ArrayList<String> methods_f= new ArrayList<>();
    for(String object: al_methods) {
      if(!al_methods_c.contains(object)){
          //json += object + ",";
          methods_f.add(object);
      }
    this.methods_f=methods_f;
    }
    //if(json.endsWith(",")) {
    //  json = json.substring(0, json.length() - 1);
    //}
    //json += "],\"attributes\":[";
    ArrayList<String> attributes_f= new ArrayList<>();
    for(String object: al_attributes) {
      if(!al_attributes_c.contains(object)){
    	  // json += object + ",";
          attributes_f.add(object);
      }
    }
    this.attributes_f=attributes_f;
    //if(json.endsWith(",")) {
    //  json = json.substring(0, json.length() - 1);
    //}
    // missing
    //json += "]}],\"missing\":[{\"classes\":[";
    ArrayList<String> classes_m= new ArrayList<>();
    for(String object: al_classes_c) {
      if(!al_classes.contains(object)){
    	  //  json += object + ",";
          classes_m.add(object);
      }
    }
    this.classes_m=classes_m;
 // if(json.endsWith(",")) {
 //    json = json.substring(0, json.length() - 1);
 //  }
 //  json += "],\"methods\":[";
    ArrayList<String> methods_m= new ArrayList<>();
    for(String object: al_methods_c) {
      if(!al_methods.contains(object)){
    	// json += object + ",";
          methods_m.add(object);
      }
    }
    this.methods_m=methods_m;
    // if(json.endsWith(",")) {
    // json = json.substring(0, json.length() - 1);
    // }
    //json += "],\"attributes\":[";
    ArrayList<String> attributes_m= new ArrayList<>();
    for(String object: al_attributes_c) {
      if(!al_attributes.contains(object)){
    	  //    json += object + ",";
          attributes_m.add(object);
      }
    }
    this.attributes_m=attributes_m;
    // if(json.endsWith(",")) {
    //  json = json.substring(0, json.length() - 1);
    // }
    // json += "]}]}";
    // Logger.debug(json);
    //  this.json = json;
  }
  
  public String getClasses_c(){
	  String ret="";
	  Iterator<String> it = this.classes_c.listIterator(0);
	  while(it.hasNext()){
		  ret+=it.next()+"\n";
	  }
	  return ret;
  }
  public String getClasses_f(){
	  String ret="";
	  Iterator<String> it = this.classes_f.listIterator(0);
	  while(it.hasNext()){
		  ret+=it.next()+"\n";
	  }
	  return ret;
  }
  public String getClasses_m(){
	  String ret="";
	  Iterator<String> it = this.classes_m.listIterator(0);
	  while(it.hasNext()){
		  ret+=it.next()+"\n";
	  }
	  return ret;
  }
  public String getMethods_c(){
	  String ret="";
	  Iterator<String> it = this.methods_c.listIterator(0);
	  while(it.hasNext()){
		  ret+=it.next()+"\n";
	  }
	  return ret;
  }
  public String getMethods_f(){
	  String ret="";
	  Iterator<String> it = this.methods_f.listIterator(0);
	  while(it.hasNext()){
		  ret+=it.next()+"\n";
	  }
	  return ret;
  }
  public String getMethods_m(){
	  String ret="";
	  Iterator<String> it = this.methods_m.listIterator(0);
	  while(it.hasNext()){
		  ret+=it.next()+"\n";
	  }
	  return ret;
  }
  public String getAttributes_c(){
	  String ret="";
	  Iterator<String> it = this.attributes_c.listIterator(0);
	  while(it.hasNext()){
		  ret+=it.next()+"\n";
	  }
	  return ret;
  }
  public String getAttributes_f(){
	  String ret="";
	  Iterator<String> it = this.attributes_f.listIterator(0);
	  while(it.hasNext()){
		  ret+=it.next()+"\n";
	  }
	  return ret;
  }
  public String getAttributes_m(){
	  String ret="";
	  Iterator<String> it = this.attributes_m.listIterator(0);
	  while(it.hasNext()){
		  ret+=it.next()+"\n";
	  }
	  return ret;
  }
  public String getSolution() {
    return this.json;
  }
  public void setTitleExcercise(String title){
	  this.title=title;
  }
  public String getTitleExercise(){
	  return title;
  }
  
  
}
