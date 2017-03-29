package model;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UmlExTextParser {

  // Java char classes do not contain german Umlauts
  private static final Pattern CAP_WORDS = Pattern.compile("[A-Z][a-zäöü]*");

  private String rawText;

  private Map<String, String> mappings;

  private Map<String, String> methods;

  private List<String> toIgnore;

  public UmlExTextParser(String theRawText, Map<String, String> theMappings, Map<String, String> theMethods,
      List<String> theToIgnore) {
    rawText = theRawText;
    mappings = theMappings;
    methods = theMethods;
    toIgnore = theToIgnore;
  }

  private static String replaceWithMappingSpan(String text, String key, String value) {
    return text.replaceAll(key,
        "<span class=\"nomen\" onclick=\"mark(this)\" data-baseform=\"" + value + "\">" + key + "</span>");
  }

  public String parseTextForClassSel() {
    // FIXME: bug with Station/Station(en)! --> use java.(util?).regex.Pattern
    List<String> simpleReplacements = readSimpleReplacements();

    String newText = rawText;

    for(String simpleRep: simpleReplacements)
      newText = newText.replaceAll(simpleRep, "<span class=\"nomen\" onclick=\"mark(this)\">" + simpleRep + "</span>");

    for(Map.Entry<String, String> classMapping: mappings.entrySet())
      newText = replaceWithMappingSpan(newText, classMapping.getKey(), classMapping.getValue());

    for(Map.Entry<String, String> methodMapping: methods.entrySet())
      newText = replaceWithMappingSpan(newText, methodMapping.getKey(), methodMapping.getValue());

    return newText;
  }

  private List<String> readSimpleReplacements() {
    Matcher matcher = CAP_WORDS.matcher(rawText);

    List<String> capitalizedWords = new LinkedList<>();
    while(matcher.find())
      capitalizedWords.add(rawText.substring(matcher.start(), matcher.end()));

    // @formatter:off
    return capitalizedWords.stream()
        .distinct()
        .filter(word -> !toIgnore.contains(word))
        .sorted()
        .collect(Collectors.toList());
    // @formatter:on
  }

}
