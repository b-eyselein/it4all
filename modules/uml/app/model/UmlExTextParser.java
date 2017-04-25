package model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UmlExTextParser {

  private static final String CSS_CLASS_NAME = "non-marked";

  private static final String CLASS_SELECTION_FUNCTION = "onclick=\"select(this)\"";

  private static final String DIAG_DRAWING_FUNCTION = "draggable=\"true\" ondragstart=\"drag(event)\"";

  private static final Pattern CAP_WORDS = Pattern.compile("[A-Z][a-zäöüß]*");

  private String rawText;
  
  private Map<String, String> mappings;
  private List<String> simpleReplacements;

  public UmlExTextParser(String theRawText, Map<String, String> theMappings, List<String> theToIgnore) {
    rawText = theRawText;
    mappings = theMappings;
    simpleReplacements = getCapitalizedWords().stream()
        .filter(key -> !mappings.keySet().contains(key) && !theToIgnore.contains(key)).collect(Collectors.toList());
  }

  public static Collection<String> getCapitalizedWords(String rawText) {
    Matcher matcher = CAP_WORDS.matcher(rawText);

    Set<String> capitalizedWords = new TreeSet<>();
    while(matcher.find())
      capitalizedWords.add(rawText.substring(matcher.start(), matcher.end()));

    return capitalizedWords;
  }

  private static String replaceWithMappingSpan(String text, String key, String value, String function) {
    Matcher matcher = Pattern.compile(key + "\\b").matcher(text);
    return matcher.replaceAll(
        "<span class=\"" + CSS_CLASS_NAME + "\" " + function + " data-baseform=\"" + value + "\">" + key + "</span>");
  }

  public List<String> getCapitalizedWords() {
    return new LinkedList<>(getCapitalizedWords(rawText));
  }

  public String parseTextForClassSel() {
    return parseText(CLASS_SELECTION_FUNCTION);
  }

  public String parseTextForDiagDrawing() {
    return parseText(DIAG_DRAWING_FUNCTION);
  }

  private String parseText(String function) {
    String newText = rawText;

    for(String simpleRep: simpleReplacements)
      newText = replaceWithMappingSpan(newText, simpleRep, simpleRep, function);

    for(Map.Entry<String, String> mapping: mappings.entrySet())
      newText = replaceWithMappingSpan(newText, mapping.getKey(), mapping.getValue(), function);

    return newText;
  }

}
