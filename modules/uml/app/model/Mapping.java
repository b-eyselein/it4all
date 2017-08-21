package model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.databind.JsonNode;

import io.ebean.Model;

@Entity
public class Mapping extends Model {
  
  public String mappedKey;
  
  public String mappedValue;
  
  @ManyToOne
  public UmlExercise exercise;
  
  public Mapping(String theKey, String theMappedTo) {
    mappedKey = theKey;
    mappedValue = theMappedTo;
  }
  
  public static Mapping fromJson(JsonNode node) {
    return new Mapping(node.get("mappedKey").asText(), node.get("mappedValue").asText());
  }
  
}
