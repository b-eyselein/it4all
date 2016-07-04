package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Model;

@Entity
public class SqlExercise extends Model {
  
  public static Finder<Integer, SqlExercise> finder = new Finder<Integer, SqlExercise>(SqlExercise.class);

  @Id
  public int id;

  public String title;

  @Column(columnDefinition = "text")
  public String text;

  public String sample;

}
