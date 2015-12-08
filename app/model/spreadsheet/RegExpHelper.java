package model.spreadsheet;

import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Stefan Olbrecht
 *
 */
public class RegExpHelper {
  
  public static String getCellFormulasDiff(String string1, String string2) {
    HashSet<String> al1 = RegExpHelper.getCellFormulasList(string1);
    HashSet<String> al2 = RegExpHelper.getCellFormulasList(string2);
    Collection<String> diff = HashSetHelper.getDifferenceOfCollections(al1, al2);
    return diff.toString();
  }
  
  private static HashSet<String> getCellFormulasList(String string) {
    Pattern p = Pattern.compile("([A-Z]+)[(]");
    Matcher m = p.matcher(string);
    HashSet<String> matches = new HashSet<String>();
    while(m.find()) {
      matches.add(m.group(1));
    }
    return matches;
  }
  
  public static String getCellOperatorsDiff(String string1, String string2) {
    HashSet<String> al1 = RegExpHelper.getCellOperatorsList(string1);
    HashSet<String> al2 = RegExpHelper.getCellOperatorsList(string2);
    Collection<String> diff = HashSetHelper.getDifferenceOfCollections(al1, al2);
    return diff.toString();
  }
  
  private static HashSet<String> getCellOperatorsList(String string) {
    Pattern p = Pattern.compile("([+|-]|[*|/])");
    Matcher m = p.matcher(string);
    HashSet<String> matches = new HashSet<String>();
    while(m.find()) {
      matches.add(m.group(1));
    }
    return matches;
  }
  
  public static String getCellRangesDiff(String string1, String string2) {
    HashSet<String> al1 = RegExpHelper.getCellRangesList(string1);
    HashSet<String> al2 = RegExpHelper.getCellRangesList(string2);
    Collection<String> diff = HashSetHelper.getDifferenceOfCollections(al1, al2);
    return diff.toString();
  }
  
  private static HashSet<String> getCellRangesList(String string) {
    Pattern p = Pattern.compile("([A-Z]+[0-9]+)");
    string = string.replace("$", "");
    Matcher m = p.matcher(string);
    HashSet<String> matches = new HashSet<String>();
    while(m.find()) {
      matches.add(m.group(1));
    }
    return matches;
  }
  
  public static String getExcelCFFormulaList(String string) {
    String cf = "";
    Pattern p = Pattern.compile("<main:formula>(.*?)</main:formula>");
    Matcher m = p.matcher(string);
    if(m.find()) {
      String match = m.group(1);
      cf = match.replace("$", "");
    }
    return cf;
  }
  
  public static String getExcelChartRangesDiff(String name1, String string1, String name2, String string2) {
    HashSet<String> al1 = RegExpHelper.getExcelChartRangesList(name1, string1);
    HashSet<String> al2 = RegExpHelper.getExcelChartRangesList(name2, string2);
    Collection<String> diff = HashSetHelper.getDifferenceOfCollections(al1, al2);
    return diff.toString();
  }
  
  private static HashSet<String> getExcelChartRangesList(String name, String string) {
    Pattern p = Pattern.compile("<c:f>" + name + "!(.*?)</c:f>");
    Matcher m = p.matcher(string);
    HashSet<String> matches = new HashSet<String>();
    while(m.find()) {
      String match = m.group(1);
      match = match.replace("$", "");
      matches.add(match);
    }
    return matches;
  }
  
  public static String getExcelChartTitle(String string) {
    String title = "";
    Pattern p = Pattern.compile("<a:t>(.*?)</a:t>");
    Matcher m = p.matcher(string);
    if(m.find()) {
      title = m.group(1);
    }
    return title;
  }
  
  public static boolean getPatternInString(String pattern, String string) {
    Pattern p = Pattern.compile(".*?(" + pattern + ").*?");
    Matcher m = p.matcher(string);
    if(m.find()) {
      return true;
    }
    return false;
  }
  
  public static void main(String[] args) {
    String string1 = "<main:formula>$G$2/$G$9</main:formula></main:cfRule></xml-fragment>";
    String string2 = "<main:formula>($H$2:$H$9)</main:formula></main:cfRule></xml-fragment>";
    string1 = RegExpHelper.getExcelCFFormulaList(string1);
    string2 = RegExpHelper.getExcelCFFormulaList(string2);
    System.out.println(StringHelper.getDiffOfTwoFormulas(string1, string2));
  }
  
}
