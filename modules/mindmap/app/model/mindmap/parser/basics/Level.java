package model.mindmap.parser.basics;

public class Level {
  
  public static int getDepth(String level) {
    switch(level) {
    case ("Titel"):
      return 0;
    case "berschrift1":
      return 1;
    case "berschrift2":
      return 2;
    case "berschrift3":
      return 3;
    case "berschrift4":
      return 4;
    case "berschrift5":
      return 5;
    case "berschrift6":
      return 6;
    case "berschrift7":
      return 7;
    case "berschrift8":
      return 8;
    case "berschrift9":
      return 9;
    default:
      return -1;
    }
  }

  public static String getLevelEnglish(int i) {
    if(i == 0)
      return "Title";
    else if(1 <= i && i < 9)
      return "Heading" + Integer.toString(i);
    else
      return null;
  }
  
  public static String getLevelGerman(int i) {
    if(i == 0)
      return "Titel";
    else if(1 <= i && i < 9)
      return "berschrift" + Integer.toString(i);
    else
      return null;
  }
  
  private Level() {

  }
}
