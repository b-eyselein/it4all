package model.html.result;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import model.html.result.ElementResult;
import model.html.result.MultiNameResult;
import model.html.task.Task;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class MultiNameResultTest {
  
  private static final String ELEMENT_NAME = "test_name";
  
  private static final String TAG_NAME = "input";
  private static final String TAG_NAME_FALSE = "button";
  
  private static final String ATT_1_KEY = "type";
  private static final String ATT_1_VALUE = "radio";
  
  private static final String ATT_2_KEY = "value";
  private static final String ATT_2_VALUE_1 = "val_1";
  private static final String ATT_2_VALUE_2 = "val_2";
  private static final String ATT_2_VALUE_3 = "val_3";
  private static final String ATT_2_VALUE_FALSE = "false";
  
  private static final String COMMON_ATTRIBUTES = ATT_1_KEY + "=" + ATT_1_VALUE;
  private static final String DEFINING_ATTRIBUTES = ATT_2_KEY + "=" + ATT_2_VALUE_1 + ";" + ATT_2_KEY + "="
      + ATT_2_VALUE_2 + ";" + ATT_2_KEY + "=" + ATT_2_VALUE_3;
  
  private static final Task TASK = mock(Task.class);
  
  private static WebDriver webDriver = mock(WebDriver.class);
  private static WebElement element1 = mock(WebElement.class);
  private static WebElement element2 = mock(WebElement.class);
  private static WebElement element3 = mock(WebElement.class);
  
  @Before
  public void setUp() {
    when(element1.getAttribute("name")).thenReturn(ELEMENT_NAME);
    when(element1.getTagName()).thenReturn(TAG_NAME);
    when(element1.getAttribute(ATT_1_KEY)).thenReturn(ATT_1_VALUE);
    when(element1.getAttribute(ATT_2_KEY)).thenReturn(ATT_2_VALUE_1);
    
    when(element2.getAttribute("name")).thenReturn(ELEMENT_NAME);
    when(element2.getTagName()).thenReturn(TAG_NAME);
    when(element2.getAttribute(ATT_1_KEY)).thenReturn(ATT_1_VALUE);
    when(element2.getAttribute(ATT_2_KEY)).thenReturn(ATT_2_VALUE_2);
    
    when(element3.getAttribute("name")).thenReturn(ELEMENT_NAME);
    when(element3.getTagName()).thenReturn(TAG_NAME);
    when(element3.getAttribute(ATT_1_KEY)).thenReturn(ATT_1_VALUE);
    when(element3.getAttribute(ATT_2_KEY)).thenReturn(ATT_2_VALUE_3);
  }
  
  @Test
  public void testElementWithAllAttributes() {
    when(webDriver.findElements(By.name(ELEMENT_NAME))).thenReturn(Arrays.asList(element1, element2, element3));
    
    MultiNameResult result = new MultiNameResult(TASK, TAG_NAME, ELEMENT_NAME, COMMON_ATTRIBUTES,
        DEFINING_ATTRIBUTES);
    assertNotNull(result);
    result.evaluate(webDriver);
    assertNotNull(result);
    assertThat(result.getSuccess(), equalTo(ElementResult.Success.COMPLETE));
  }
  
  @Test
  public void testElementWithEmptyCOMMONAttributes() {
    when(webDriver.findElements(By.name(ELEMENT_NAME))).thenReturn(Arrays.asList(element1, element2, element3));
    
    MultiNameResult result = new MultiNameResult(TASK, TAG_NAME, ELEMENT_NAME, COMMON_ATTRIBUTES
        + ";abcde;", DEFINING_ATTRIBUTES);
    assertNotNull(result);
    result.evaluate(webDriver);
    assertNotNull(result);
    assertThat(result.getSuccess(), equalTo(ElementResult.Success.COMPLETE));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testElementWithOutAttributes() {
    when(webDriver.findElements(By.name(ELEMENT_NAME))).thenReturn(Arrays.asList(element1, element2));
    new MultiNameResult(TASK, TAG_NAME, ELEMENT_NAME, "", "");
  }
  
  @Test
  public void testNoElementsWithDEFININGAttributes() {
    when(element1.getAttribute(ATT_2_KEY)).thenReturn(ATT_2_VALUE_FALSE);
    when(element2.getAttribute(ATT_2_KEY)).thenReturn(ATT_2_VALUE_FALSE);
    when(webDriver.findElements(By.name(ELEMENT_NAME))).thenReturn(Arrays.asList(element1, element2));
    
    MultiNameResult result = new MultiNameResult(TASK, TAG_NAME, ELEMENT_NAME, COMMON_ATTRIBUTES,
        DEFINING_ATTRIBUTES);
    assertNotNull(result);
    result.evaluate(webDriver);
    assertNotNull(result);
    assertThat(result.getSuccess(), equalTo(ElementResult.Success.NONE));
  }
  
  @Test
  public void testNoElementsWithMatchingName() {
    when(webDriver.findElements(By.name(ELEMENT_NAME))).thenReturn(Collections.emptyList());
    
    MultiNameResult result = new MultiNameResult(TASK, TAG_NAME, ELEMENT_NAME, COMMON_ATTRIBUTES,
        DEFINING_ATTRIBUTES);
    result.evaluate(webDriver);
    
    assertNotNull(result);
    assertThat(result.getSuccess(), equalTo(ElementResult.Success.NONE));
    assertThat(result.getMessage(), equalTo("Es wurde kein Element mit dem Namen \"" + ELEMENT_NAME + "\" gefunden"));
  }
  
  @Test
  public void testNoElementsWithMatchingTag() {
    WebDriver webDriver = mock(WebDriver.class);
    
    when(element1.getTagName()).thenReturn(TAG_NAME_FALSE);
    when(element2.getTagName()).thenReturn(TAG_NAME_FALSE);
    when(element3.getTagName()).thenReturn(TAG_NAME_FALSE);
    when(webDriver.findElements(By.name(ELEMENT_NAME))).thenReturn(Arrays.asList(element1, element2, element3));
    
    MultiNameResult result = new MultiNameResult(TASK, TAG_NAME, ELEMENT_NAME, COMMON_ATTRIBUTES,
        DEFINING_ATTRIBUTES);
    result.evaluate(webDriver);
    
    assertNotNull(result);
    assertThat(result.getSuccess(), equalTo(ElementResult.Success.NONE));
    assertThat(result.getMessage(), equalTo("Keines der gefundenen Elemente hat den passenden Tag \"" + TAG_NAME
        + "\"!"));
  }
  
  @Test
  public void testNotEnoughElementsWithDEFININGAttributes() {
    when(webDriver.findElements(By.name(ELEMENT_NAME))).thenReturn(Arrays.asList(element1, element2));
    
    MultiNameResult result = new MultiNameResult(TASK, TAG_NAME, ELEMENT_NAME, COMMON_ATTRIBUTES,
        DEFINING_ATTRIBUTES);
    assertNotNull(result);
    result.evaluate(webDriver);
    assertNotNull(result);
    assertThat(result.getSuccess(), equalTo(ElementResult.Success.PARTIALLY));
  }

  @Test
  public void testEvaluate() throws Exception {
    throw new RuntimeException("not yet implemented");
  }
  
}
