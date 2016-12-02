package model.user;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class Settings {
  
  public enum TODO {
    SHOW("Einblenden"), AGGREGATE("Zusammenfassen"), HIDE("Ausblenden");
    
    private String german;
    
    private TODO(String theGerman) {
      german = theGerman;
    }
    
    public String getGerman() {
      return german;
    }
  }
  
  @Enumerated(EnumType.STRING)
  public TODO todo = TODO.SHOW;
  
}
