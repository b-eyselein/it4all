package model.html;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import model.Task;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ElementResultByNameTest {
  
  private static final String ELEMENT_NAME = "email";
  private static final String TAG_NAME = "input";
  private static final String TAG_NAME_FALSE = "button";
  private static final String ATTRIBUTES = "";
  
  private static final Task TASK = mock(Task.class);
  private static WebDriver webDriver = mock(WebDriver.class);
  private static WebElement element1 = mock(WebElement.class), element2 = mock(WebElement.class);
  private static List<WebElement> foundElementsForElementName = Arrays.asList(element1, element2);
  
  @BeforeClass
  public static void completeSetup() {
    when(element1.getTagName()).thenReturn(TAG_NAME_FALSE);
    when(element2.getTagName()).thenReturn(TAG_NAME);
    when(webDriver.findElements(By.name(ELEMENT_NAME))).thenReturn(foundElementsForElementName);
  }
  
  @Test
  public void testElementResultByName() {
    ElementResultByName result = new ElementResultByName(TASK, TAG_NAME, ELEMENT_NAME, ATTRIBUTES);
    assertNotNull(result);
    result.evaluate(webDriver);
    
    assertTrue(result.rightTagNameWasUsed());
  }
  
}
