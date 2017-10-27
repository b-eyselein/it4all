package model.mindmap.parser.basics;

public class Level {

  private Level() {

  }

  public static int getDepth(String level) {
    if("Titel".equals(level))
      return 0;
    else if(level.startsWith("berschrift"))
      return Integer.parseInt(Character.toString(level.charAt(level.length() - 1)));
    else
      return -1;
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
}
