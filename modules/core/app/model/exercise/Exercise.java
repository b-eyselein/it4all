package model.exercise;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import com.avaje.ebean.Model;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type", length = 10)
public abstract class Exercise extends Model {
  
  // FIXME: PK: type + int!
  @Id
  public int id;
  
  public String title;
  
  @Column(name = "exerciseText", columnDefinition = "text")
  public String exerciseText;
  
  public abstract int getMaxPoints();
  
}
