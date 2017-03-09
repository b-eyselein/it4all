package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

public class UmlClassselection {

  private String title;	
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
    JsonNode node_c = Json.parse(
        "{\"classes\":[\"Telekonverter\",\"Profigehäuse\",\"Kameragehäuse\",\"Amateurgehäuse\",\"Profiblitz\",\"Objektiv\",\"Amateurblitz\",\"Sonnenblende\",\"Zoomobjektiv\",\"Festweitenobjektiv\"],\"methods\":[\"Hersteller\",\"Fotosystems\"],\"attributes\":[\"Ikonograf\",\"Webseite\"]}");
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
    // correct
    ArrayList<String> al_classes = new ArrayList<String>(Arrays.asList(arr_classes));
    ArrayList<String> al_classes_c = new ArrayList<String>(Arrays.asList(arr_classes_c));
    ArrayList<String> classes_c= new ArrayList<>();
    for(String object: al_classes) {
      if(al_classes_c.contains(object)) {
        classes_c.add(object);
      }
    }
    this.classes_c=classes_c;
    ArrayList<String> al_methods = new ArrayList<String>(Arrays.asList(arr_methods));
    ArrayList<String> al_methods_c = new ArrayList<String>(Arrays.asList(arr_methods_c));
    ArrayList<String> methods_c= new ArrayList<>();
    for(String object: al_methods) {
      if(al_methods_c.contains(object)) {
        methods_c.add(object);
      }
    }
    this.methods_c=methods_c;
    ArrayList<String> al_attributes = new ArrayList<String>(Arrays.asList(arr_attributes));
    ArrayList<String> al_attributes_c = new ArrayList<String>(Arrays.asList(arr_attributes_c));
    ArrayList<String> attributes_c= new ArrayList<>();
    for(String object: al_attributes) {
      if(al_attributes_c.contains(object)) {
        attributes_c.add(object);
      }
    }
    this.attributes_c=attributes_c;
    ArrayList<String> classes_f= new ArrayList<>();
    for(String object: al_classes) {
      if(!al_classes_c.contains(object)){
          classes_f.add(object);
      }
    this.classes_f=classes_f;
    }
    ArrayList<String> methods_f= new ArrayList<>();
    for(String object: al_methods) {
      if(!al_methods_c.contains(object)){
          methods_f.add(object);
      }
    this.methods_f=methods_f;
    }
    ArrayList<String> attributes_f= new ArrayList<>();
    for(String object: al_attributes) {
      if(!al_attributes_c.contains(object)){
          attributes_f.add(object);
      }
    }
    this.attributes_f=attributes_f;
    // missing
    ArrayList<String> classes_m= new ArrayList<>();
    for(String object: al_classes_c) {
      if(!al_classes.contains(object)){
          classes_m.add(object);
      }
    }
    this.classes_m=classes_m;
    ArrayList<String> methods_m= new ArrayList<>();
    for(String object: al_methods_c) {
      if(!al_methods.contains(object)){
          methods_m.add(object);
      }
    }
    this.methods_m=methods_m;
    ArrayList<String> attributes_m= new ArrayList<>();
    for(String object: al_attributes_c) {
      if(!al_attributes.contains(object)){
          attributes_m.add(object);
      }
    }
    this.attributes_m=attributes_m;
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
  
  public void setTitleExcercise(String title){
	  this.title=title;
  }
  public String getTitleExercise(){
	  return title;
  }
  
  
}
