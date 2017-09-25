package model;

import java.util.List;
import java.util.Random;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import io.ebean.Finder;
import io.ebean.Model;

@Entity
@Table(name = "tipps")
public class Tipp extends Model {
  
  private static final Random RAN = new Random();
  
  public static final String STD_TIPP = "Hier werden in Zukunft Tipps & Tricks zur Benutzung von it4all präsentiert.";
  
  public static final Finder<Integer, Tipp> finder = new Finder<>(Tipp.class);
  
  @Id
  public int id;
  
  @Column
  public String str;
  
  public static String getRandom() {
    List<Tipp> allTipps = finder.all();
    
    if(allTipps.isEmpty())
      return STD_TIPP;
    
    return allTipps.get(RAN.nextInt(allTipps.size())).str;
  }
  
}