package model.exercise;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.avaje.ebean.Model;

@Entity
public class SqlScenario extends Model {

  public static Finder<Integer, SqlScenario> finder = new Finder<>(SqlScenario.class);

  @Id
  public String name;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "scenario")
  public List<SqlExercise> exercises;

}
