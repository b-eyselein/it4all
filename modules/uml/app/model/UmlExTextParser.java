package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UmlExTextParser {
  
  private static final String CSS_CLASS_NAME = "nonMarked";
  private static final String FUNCTION = "onclick=\"select(this)\"";
  private static final Pattern CAP_WORDS = Pattern.compile("[A-Z][a-zäöüß]*");
  
  private String rawText;
  private Map<String, String> mappings;
  private List<String> toIgnore;
  
  public UmlExTextParser(String theRawText, Map<String, String> theMappings, List<String> theToIgnore) {
    rawText = theRawText;
    mappings = theMappings;
    toIgnore = theToIgnore;
  }
  
  private static String replaceWithMappingSpan(String text, String key, String value) {
    Matcher matcher = Pattern.compile(key + "\\b").matcher(text);
    return matcher.replaceAll(
        "<span class=\"" + CSS_CLASS_NAME + "\" " + FUNCTION + " data-baseform=\"" + value + "\">" + key + "</span>");
  }
  
  public String parseTextForClassSel() {
    String newText = rawText;
    
    for(Map.Entry<String, String> simpleRep: readReplacements().entrySet())
      newText = replaceWithMappingSpan(newText, simpleRep.getKey(), simpleRep.getValue());
    
    for(Map.Entry<String, String> mapping: mappings.entrySet())
      newText = replaceWithMappingSpan(newText, mapping.getKey(), mapping.getValue());
    
    return newText;
  }
  
  private Map<String, String> readReplacements() {
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
