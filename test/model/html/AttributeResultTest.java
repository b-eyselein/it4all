package model.html;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.openqa.selenium.WebElement;

public class AttributeResultTest {
  
  private static final String ATTRIBUTE_NAME = "type";
  private static final String ATTRIBUTE_NAME_FALSE = "hidden";
  private static final String ATTRIBUTE_VALUE = "email";
  private static final String ATTRIBUTE_VALUE_FALSE = "text";
  
  @Test
  public void testAttributeResult() {
    WebElement webElement = mock(WebElement.class);
    when(webElement.getAttribute(ATTRIBUTE_NAME)).thenReturn(ATTRIBUTE_VALUE);
    when(webElement.getAttribute(ATTRIBUTE_NAME_FALSE)).thenReturn(null);
    
    AttributeResult res = new AttributeResult(webElement, ATTRIBUTE_NAME, ATTRIBUTE_VALUE);
    assertNotNull(res);
    assertTrue(res.isFound());
    assertThat(res.getAttributeName(), equalTo(ATTRIBUTE_NAME));
    assertThat(res.getAttributeValue(), equalTo(ATTRIBUTE_VALUE));
    
    res = new AttributeResult(webElement, ATTRIBUTE_NAME, ATTRIBUTE_VALUE_FALSE);
    assertNotNull(res);
    assertFalse(res.isFound());
    assertThat(res.getAttributeName(), equalTo(ATTRIBUTE_NAME));
    assertThat(res.getAttributeValue(), equalTo(ATTRIBUTE_VALUE_FALSE));
    
    res = new AttributeResult(webElement, ATTRIBUTE_NAME_FALSE, ATTRIBUTE_VALUE);
    assertNotNull(res);
    assertFalse(res.isFound());
    assertThat(res.getAttributeName(), equalTo(ATTRIBUTE_NAME_FALSE));
    assertThat(res.getAttributeValue(), equalTo(ATTRIBUTE_VALUE));
  }
  
}
