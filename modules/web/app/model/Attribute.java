package model;

import java.util.List;

import org.openqa.selenium.WebElement;

import com.google.common.base.Splitter;

import model.result.AttributeResult;

public class Attribute {
  
  public static final String KEY_VALUE_CHARACTER = "=";
  
  private static final Splitter KEY_VALUE_SPLITTER = Splitter.on(KEY_VALUE_CHARACTER).omitEmptyStrings();
  
  public String key;
  
  public String value;
  
  public Attribute() {
    // Empty constructor for Json.fromJson(...)
  }
  
  public Attribute(String theKey, String theValue) {
    key = theKey;
    value = theValue;
  }
  
  public static Attribute fromString(String keyEqualsValue) {
    if(!keyEqualsValue.contains(KEY_VALUE_CHARACTER))
      return new Attribute(keyEqualsValue, keyEqualsValue);
    
    List<String> keyAndValue = KEY_VALUE_SPLITTER.splitToList(keyEqualsValue);
    return new Attribute(keyAndValue.get(0), keyAndValue.get(1));
  }
  
  public AttributeResult evaluate(WebElement element) {
    try {
      String foundValue = element.getAttribute(key);
      return new AttributeResult(this, foundValue);
    } catch (NoSuchMethodError e) { // NOSONAR
      return new AttributeResult(this, null);
    }
  }
  
  public String forDB() {
    return key + KEY_VALUE_CHARACTER + value;
  }
  
}
