package model.user;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

import model.exercise.Exercise;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "extype")
public abstract class ExerciseResult<E extends Exercise> extends Model {
  
  @ManyToOne(cascade = CascadeType.ALL)
  public User user;
  
  public E exercise; // NOSONAR
  
}
