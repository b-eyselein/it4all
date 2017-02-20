package controllers.uml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import controllers.core.ExerciseController;
import controllers.core.UserManagement;
import model.IntExerciseIdentifier;
import model.UmlExercise;
import model.Util;
import model.result.CompleteResult;
import model.user.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Http.Request;
import play.mvc.Result;
import scala.collection.immutable.List;
import scala.util.parsing.json.JSONArray;
import views.html.classselection;
import views.html.diagramdrawing;
import views.html.umloverview;
import views.html.difficulty;

public class UML extends ExerciseController<IntExerciseIdentifier> {

  @Inject
  public UML(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }
  
  private static String getExerciseText() {
    try {
      Path file = Paths.get("modules/uml/conf/exerciseText.txt");
      Logger.debug(file.toAbsolutePath().toString());
      return String.join("\n", Files.readAllLines(file));
      
    } catch (IOException e) {
      return "TODO!";
    }
  }
  
  public Result classSelection(IntExerciseIdentifier identifier) {
    return ok(classselection.render(UserManagement.getCurrentUser()));
  }
  
  public Result correct() {
    DynamicForm form = factory.form().bindFromRequest();
    String classes = form.get("fname");
    JsonNode node = Json.parse(classes);
    node.get("classes");
    JsonNode node_c = Json.parse("{\"classes\":[\"Canikuji\",\"Firma\"],\"methods\":[\"Hersteller\",\"Fotosystems\"],\"attributes\":[\"Ikonograf\",\"Webseite\"]}");
    Logger.debug(Json.prettyPrint(node));
    Logger.debug("getclasses: "+Json.prettyPrint(node.get("classes")));
    Logger.debug("getmethods: "+Json.prettyPrint(node.get("methods")));
    Logger.debug("getattr: "+Json.prettyPrint(node.get("attributes")));
   // Init
    String[] arr_classes = node.get("classes").toString().substring(1, node.get("classes").toString().length()-1).split(",");
    String[] arr_methods = node.get("methods").toString().substring(1, node.get("methods").toString().length()-1).split(",");
    String[] arr_attributes = node.get("attributes").toString().substring(1, node.get("attributes").toString().length()-1).split(",");
    String[] arr_classes_c = node_c.get("classes").toString().substring(1, node_c.get("classes").toString().length()-1).split(",");
    String[] arr_methods_c = node_c.get("methods").toString().substring(1, node_c.get("methods").toString().length()-1).split(",");
    String[] arr_attributes_c = node_c.get("attributes").toString().substring(1, node_c.get("attributes").toString().length()-1).split(",");

    String json ="{\"correct\":[{\"classes\":[";    
    //correct
    ArrayList<String> al_classes= new ArrayList<String>(Arrays.asList(arr_classes));
    ArrayList<String> al_classes_c= new ArrayList<String>(Arrays.asList(arr_classes_c)); 
    for (String object: al_classes) {
    	if(al_classes_c.contains(object)){
    		Logger.debug("Klasse: "+object+" stimmt.");
    		json+=object+",";
    	}
    }
    if(json.endsWith(",")){
    	json=json.substring(0, json.length()-1);
    }
    json+="],\"methods\":[";
    ArrayList<String> al_methods= new ArrayList<String>(Arrays.asList(arr_methods));
    ArrayList<String> al_methods_c= new ArrayList<String>(Arrays.asList(arr_methods_c)); 
    for (String object: al_methods) {
    	if(al_methods_c.contains(object)){
    		Logger.debug("Methode: "+object+" stimmt.");
    		json+=object+",";
    	}
    }
    if(json.endsWith(",")){
    	json=json.substring(0, json.length()-1);
    }
    json+="],\"attributes\":[";
    ArrayList<String> al_attributes= new ArrayList<String>(Arrays.asList(arr_attributes));
    ArrayList<String> al_attributes_c= new ArrayList<String>(Arrays.asList(arr_attributes_c)); 
    for (String object: al_attributes) {
    	if(al_attributes_c.contains(object)){
    		Logger.debug("Attribut: "+object+" stimmt.");
    		json+=object+",";
    	}
    }
    if(json.endsWith(",")){
    	json=json.substring(0, json.length()-1);
    }
    //false
    json+="]}],\"false\":[{\"classes\":[";
    for (String object: al_classes) {
    	if(!al_classes_c.contains(object))
    	json+=object+",";
    }
    if(json.endsWith(",")){
    	json=json.substring(0, json.length()-1);
    }
    json+="],\"methods\":[";
    for (String object: al_methods) {
    	if(!al_methods_c.contains(object))
    	json+=object+",";
    }
    if(json.endsWith(",")){
    	json=json.substring(0, json.length()-1);
    }
    json+="],\"attributes\":[";
    for (String object: al_attributes) {
    	if(!al_attributes_c.contains(object))
    	json+=object+",";
    }
    if(json.endsWith(",")){
    	json=json.substring(0, json.length()-1);
    }
    //missing
    json+="]}],\"missing\":[{\"classes\":[";
    for (String object: al_classes_c) {
    	if(!al_classes.contains(object))
    	json+=object+",";
    }
    if(json.endsWith(",")){
    	json=json.substring(0, json.length()-1);
    }
    json+="],\"methods\":[";
    for (String object: al_methods_c) {
    	if(!al_methods.contains(object))
    	json+=object+",";
    }
    if(json.endsWith(",")){
    	json=json.substring(0, json.length()-1);
    }
    json+="],\"attributes\":[";
    for (String object: al_attributes_c) {
    	if(!al_attributes.contains(object))
    	json+=object+",";
    }
    if(json.endsWith(",")){
    	json=json.substring(0, json.length()-1);
    }
    // optional
    /*
    json+="]}],\"optional\":[{\"classes\":[";
    for (String object: al_classes_c) {
    	json+=object+",";
    }
    if(json.endsWith(",")){
    	json=json.substring(0, json.length()-1);
    }
    json+="],\"methods\":[";
    for (String object: al_methods_c) {
    	json+=object+",";
    }
    if(json.endsWith(",")){
    	json=json.substring(0, json.length()-1);
    }
    json+="],\"attributes\":[";
    for (String object: al_attributes_c) {
    	json+=object+",";
    }
    if(json.endsWith(",")){
    	json=json.substring(0, json.length()-1);
    }
    */
    json+="]}]}";
    
    Logger.debug(json);
    return ok("ok");
  }

  public Result diagramDrawing(IntExerciseIdentifier identifier) {
    return ok(diagramdrawing.render(UserManagement.getCurrentUser(), getExerciseText()));
  }
  
  public Result diff(int exerciseId) {
    return ok(difficulty.render(UserManagement.getCurrentUser()));
  }
  
  public Result index() {
    return ok(umloverview.render(Arrays.asList(new UmlExercise()), UserManagement.getCurrentUser()));
  }
  
  @Override
  protected CompleteResult correct(Request request, User user, IntExerciseIdentifier identifier) {
    // TODO Auto-generated method stub
    return null;
  }

}
