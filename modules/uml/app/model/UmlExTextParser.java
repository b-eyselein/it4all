package model;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UmlExTextParser {

  private static final Pattern CAP_WORDS = Pattern.compile("[A-Z][a-zäöüß]*");

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
    Pattern toRep = Pattern.compile("(" + key + ")|(" + value + ")");
    Matcher matcher = toRep.matcher(text);
    return matcher
        .replaceAll("<span class=\"nomen\" onclick=\"mark(this)\" data-baseform=\"" + value + "\">" + key + "</span>");
  }

  public String parseTextForClassSel() {
    List<String> simpleReplacements = readSimpleReplacements();

    String newText = rawText;

    // FIXME: assing simple -> mapping!

    // for(String simpleRep: simpleReplacements)
    // newText = replaceWithMappingSpan(newText, simpleRep, simpleRep);

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
