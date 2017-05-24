package model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.databind.JsonNode;

@Entity
public class Mapping extends Model {

  public String key;

  public String mappedTo;

  @ManyToOne
  public UmlExercise exercise;

  public Mapping(String theKey, String theMappedTo) {
    key = theKey;
    mappedTo = theMappedTo;
  }

  public static Mapping fromJson(JsonNode node) {
    return new Mapping(node.get(StringConsts.KEY_NAME).asText(), node.get(StringConsts.VALUE_NAME).asText());
  }

}
