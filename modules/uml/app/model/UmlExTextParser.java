package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UmlExTextParser {
  
  private static final String CSS_CLASS_NAME = "non-marked";
  
  private static final String CLASS_SELECTION_FUNCTION = "onclick=\"select(this)\"";
  
  private static final String DIAG_DRAWING_FUNCTION = "draggable=\"true\" ondragstart=\"drag(event)\"";
  
  private static final Pattern CAP_WORDS = Pattern.compile("[A-Z][a-zäöüß]*");
  
  private Map<String, String> mappings;
  private List<String> toIgnore;
  
  public UmlExTextParser(Map<String, String> theMappings, List<String> theToIgnore) {
    mappings = theMappings;
    toIgnore = theToIgnore;
  }
  
  private static String replaceWithMappingSpan(String text, String key, String value, String function) {
    Matcher matcher = Pattern.compile(key + "\\b").matcher(text);
    return matcher.replaceAll(
        "<span class=\"" + CSS_CLASS_NAME + "\" " + function + " data-baseform=\"" + value + "\">" + key + "</span>");
  }
  
  private String parseText(String rawText, String function) {
    String newText = rawText;
    
    for(Map.Entry<String, String> simpleRep: readReplacements(rawText).entrySet())
      newText = replaceWithMappingSpan(newText, simpleRep.getKey(), simpleRep.getValue(), function);
    
    for(Map.Entry<String, String> mapping: mappings.entrySet())
      newText = replaceWithMappingSpan(newText, mapping.getKey(), mapping.getValue(), function);
    
    return newText;
  }
  
  public String parseTextForClassSel(String rawText) {
    return parseText(rawText, CLASS_SELECTION_FUNCTION);
  }
  
  public String parseTextForDiagDrawing(String rawText) {
    return parseText(rawText, DIAG_DRAWING_FUNCTION);
  }
  
  private Map<String, String> readReplacements(String rawText) {
    Matcher matcher = CAP_WORDS.matcher(rawText);
    
    Map<String, String> capitalizedWords = new HashMap<>();
    while(matcher.find()) {
      String key = rawText.substring(matcher.start(), matcher.end());
      if(!mappings.keySet().contains(key) && !toIgnore.contains(key))
        capitalizedWords.put(key, key);
    }
    
    return capitalizedWords;
  }
  
}
