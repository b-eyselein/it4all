package model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

import io.ebean.Finder;
import io.ebean.Model;

@Entity
public class ProgSample extends Model {
  
  public static final Finder<ProgSampleKey, ProgSample> finder = new Finder<>(ProgSample.class);
  
  @EmbeddedId
  private ProgSampleKey key;
  
  @ManyToOne
  @JsonBackReference
  public ProgExercise exercise;
  
  private String sample;
  
  public ProgSample(ProgSampleKey theKey, String theSample) {
    key = theKey;
    sample = theSample;
  }
  
  public ProgSampleKey getKey() {
    return key;
  }

  public String getSample() {
    return sample;
  }
  
  public ProgSample updateValues(ProgSampleKey theKey, String theSample) {
    key = theKey;
    sample = theSample;
    return this;
  }
  
}
