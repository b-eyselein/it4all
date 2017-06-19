package model;

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

  private RegExpHelper() {

  }

  public static String getCellFormulasDiff(String string1, String string2) {
    HashSet<String> al1 = RegExpHelper.getCellFormulasList(string1);
    HashSet<String> al2 = RegExpHelper.getCellFormulasList(string2);
    Collection<String> diff = HashSetHelper.getDifferenceOfCollections(al1, al2);
    if(diff.isEmpty())
      return "";
    else
      return diff.toString();
  }

  public static String getCellOperatorsDiff(String string1, String string2) {
    HashSet<String> al1 = RegExpHelper.getCellOperatorsList(string1);
    HashSet<String> al2 = RegExpHelper.getCellOperatorsList(string2);
    Collection<String> diff = HashSetHelper.getDifferenceOfCollections(al1, al2);
    if(diff.isEmpty())
      return "";
    else
      return diff.toString();
  }

  public static String getCellRangesDiff(String string1, String string2) {
    HashSet<String> al1 = RegExpHelper.getCellRangesList(string1);
    HashSet<String> al2 = RegExpHelper.getCellRangesList(string2);
    Collection<String> diff = HashSetHelper.getDifferenceOfCollections(al1, al2);
    if(diff.isEmpty())
      return "";
    else
      return diff.toString();
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
    if(diff.isEmpty())
      return "";
    else
      return diff.toString();
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
    Matcher m = Pattern.compile(".*?(" + pattern + ").*?").matcher(string);
    return m.find();
  }

  private static HashSet<String> getCellFormulasList(String string) {
    Pattern p = Pattern.compile("([A-Z]+)[(]");
    Matcher m = p.matcher(string);
    HashSet<String> matches = new HashSet<>();
    while(m.find()) {
      matches.add(m.group(1));
    }
    return matches;
  }

  private static HashSet<String> getCellOperatorsList(String string) {
    Pattern p = Pattern.compile("([+|-]|[*|/])");
    Matcher m = p.matcher(string);
    HashSet<String> matches = new HashSet<>();
    while(m.find()) {
      matches.add(m.group(1));
    }
    return matches;
  }

  private static HashSet<String> getCellRangesList(String string) {
    Pattern p = Pattern.compile("([A-Z]+[0-9]+)");
    String newString = string.replace("$", "");
    Matcher m = p.matcher(newString);
    HashSet<String> matches = new HashSet<>();
    while(m.find()) {
      matches.add(m.group(1));
    }
    return matches;
  }

  private static HashSet<String> getExcelChartRangesList(String name, String string) {
    Pattern p = Pattern.compile("<c:f>" + name + "!(.*?)</c:f>");
    Matcher m = p.matcher(string);
    HashSet<String> matches = new HashSet<>();
    while(m.find()) {
      String match = m.group(1);
      match = match.replace("$", "");
      matches.add(match);
    }
    return matches;
  }

}
