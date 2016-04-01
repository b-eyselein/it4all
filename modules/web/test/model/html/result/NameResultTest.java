package model.html.result;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import model.html.result.ElementResult;
import model.html.result.NameResult;
import model.html.task.Task;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class NameResultTest {
  
  private static final String ELEMENT_NAME = "email";
  
  private static final String TAG_NAME = "input";
  private static final String TAG_NAME_FALSE = "button";
  
  private static final String ATT_1_KEY = "type";
  private static final String ATT_1_VALUE = "email";
  private static final String ATT_2_KEY = "hidden";
  private static final String ATT_2_VALUE = "true";
  
  private static final String FALSE_ATTRIBUTE_KEY = "required";
  
  private static final String ATTRIBUTES = ATT_1_KEY + "=" + ATT_1_VALUE + ";" + ATT_2_KEY + "=" + ATT_2_VALUE;
  
  private static final Task TASK = mock(Task.class);
  
  private static WebDriver webDriver = mock(WebDriver.class);
  private static WebElement element1 = mock(WebElement.class), element2 = mock(WebElement.class);
  
  @Before
  public void setUp() {
    when(element1.getTagName()).thenReturn(TAG_NAME_FALSE);
    when(element1.getAttribute(ATT_1_KEY)).thenReturn(ATT_1_VALUE);
    when(element2.getTagName()).thenReturn(TAG_NAME);
    when(element2.getAttribute(ATT_1_KEY)).thenReturn(ATT_1_VALUE);
    when(element2.getAttribute(ATT_2_KEY)).thenReturn(ATT_2_VALUE);
    when(element2.getAttribute(FALSE_ATTRIBUTE_KEY)).thenReturn(ATT_1_VALUE);
  }
  
  @Test
  public void testElementWithAllAttributes() {
    when(webDriver.findElements(By.name(ELEMENT_NAME))).thenReturn(Arrays.asList(element1, element2));
    
    NameResult result = new NameResult(TASK, TAG_NAME, ELEMENT_NAME, ATTRIBUTES);
    assertNotNull(result);
    result.evaluate(webDriver);
    assertNotNull(result);
    assertThat(result.getSuccess(), equalTo(ElementResult.Success.COMPLETE));
    assertTrue(result.rightTagNameWasUsed());
  }
  
  @Test
  public void testElementWithEmptyAttribute() {
    when(webDriver.findElements(By.name(ELEMENT_NAME))).thenReturn(Arrays.asList(element1, element2));
    
    String attributes = ATTRIBUTES + ";";
    
    NameResult result = new NameResult(TASK, TAG_NAME, ELEMENT_NAME, attributes);
    assertNotNull(result);
    result.evaluate(webDriver);
    assertNotNull(result);
    assertThat(result.getSuccess(), equalTo(ElementResult.Success.COMPLETE));
    assertTrue(result.rightTagNameWasUsed());
  }
  
  @Test
  public void testElementWithOutAttributes() {
    when(webDriver.findElements(By.name(ELEMENT_NAME))).thenReturn(Arrays.asList(element1, element2));
    NameResult result = new NameResult(TASK, TAG_NAME, ELEMENT_NAME, "");
    assertNotNull(result);
    result.evaluate(webDriver);
    assertNotNull(result);
    assertThat(result.getSuccess(), equalTo(ElementResult.Success.COMPLETE));
    assertTrue(result.rightTagNameWasUsed());
  }
  
  @Test
  public void testElementWithPartAttributes() {
    when(webDriver.findElements(By.name(ELEMENT_NAME))).thenReturn(Arrays.asList(element1, element2));
    
    String attributes = ATTRIBUTES + ";" + FALSE_ATTRIBUTE_KEY + "=" + ATT_2_VALUE;
    NameResult result = new NameResult(TASK, TAG_NAME, ELEMENT_NAME, attributes);
    assertNotNull(result);
    result.evaluate(webDriver);
    assertNotNull(result);
    assertThat(result.getSuccess(), equalTo(ElementResult.Success.PARTIALLY));
    assertTrue(result.rightTagNameWasUsed());
  }
  
  @Test
  public void testMoreThanOneElementWithMatchingNameAndTag() {
    WebDriver webDriver = mock(WebDriver.class);
    when(webDriver.findElements(By.name(ELEMENT_NAME))).thenReturn(Arrays.asList(element2, element2, element2));
    
    NameResult result = new NameResult(TASK, TAG_NAME, ELEMENT_NAME, ATTRIBUTES);
    result.evaluate(webDriver);
    
    assertNotNull(result);
    assertThat(result.getSuccess(), equalTo(ElementResult.Success.COMPLETE));
    assertThat(result.getMessage(),
        equalTo("Es wurde mehr als 1 Element mit passendem Namen und passendem Tag gefunden. "
            + "Verwende das erste für weitere Korrektur. Alle Attribute wurden gefunden."));
  }
  
  @Test
  public void testNoElementsWithMatchingName() {
    WebDriver webDriver = mock(WebDriver.class);
    when(webDriver.findElements(By.name(ELEMENT_NAME))).thenReturn(Collections.emptyList());
    
    NameResult result = new NameResult(TASK, TAG_NAME, ELEMENT_NAME, ATTRIBUTES);
    result.evaluate(webDriver);
    
    assertNotNull(result);
    assertThat(result.getSuccess(), equalTo(ElementResult.Success.NONE));
    assertThat(result.getMessage(), equalTo("Es wurde kein Element mit dem Namen \"email\" gefunden"));
  }
  
  @Test
  public void testNoElementsWithMatchingTag() {
    WebDriver webDriver = mock(WebDriver.class);
    when(webDriver.findElements(By.name(ELEMENT_NAME))).thenReturn(Arrays.asList(element1));
    
    NameResult result = new NameResult(TASK, TAG_NAME, ELEMENT_NAME, ATTRIBUTES);
    result.evaluate(webDriver);
    
    assertNotNull(result);
    assertThat(result.getSuccess(), equalTo(ElementResult.Success.NONE));
    assertThat(result.getMessage(), equalTo("Keines der gefundenen Elemente hat den passenden Tag \"" + TAG_NAME
        + "\"!"));
  }
  
}
