package model.html.result;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import model.html.result.ElementResult;
import model.html.task.Task;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

public class ElementResultTest {

  private static final String TAG_NAME = "input";
  private static final Task TASK = mock(Task.class);
  private static final String ATTRIBUTES = "name=email;hidden=true";

  private static ElementResult result;

  @Before
  public void setUp() {
    result = new ElementResult(TASK, TAG_NAME, ATTRIBUTES) {
      @Override
      public void evaluate(WebDriver driver) {
        // Not tested!
      }
    };
  }

  @Test
  public void testAllAttributesFound() {
    // No attributes added --> can only be true!
    assertTrue(result.allAttributesFound());
  }

  @Test
  public void testElementResult() {
    assertNotNull(result);
  }

  @Test
  public void testGet_Set_Success_AND_GetPoints() {
    assertThat(result.getSuccess(), equalTo(ElementResult.Success.NONE));
    assertThat(result.getPoints(), equalTo(0));
    result.setResult(ElementResult.Success.PARTIALLY, "Test0");
    assertThat(result.getSuccess(), equalTo(ElementResult.Success.PARTIALLY));
    assertThat(result.getPoints(), equalTo(1));
    result.setResult(ElementResult.Success.COMPLETE, "Test1");
    assertThat(result.getSuccess(), equalTo(ElementResult.Success.COMPLETE));
    assertThat(result.getPoints(), equalTo(2));
    result.setResult(ElementResult.Success.NONE, "Test2");
    assertThat(result.getSuccess(), equalTo(ElementResult.Success.NONE));
    assertThat(result.getPoints(), equalTo(0));
  }

  @Test
  public void testGetAttributes() {
    assertNotNull(result.getAttributes());
    assertTrue(result.getAttributes().isEmpty());
  }

  @Test
  public void testGetTask() {
    assertNotNull(result.getTask());
    assertThat(result.getTask(), equalTo(TASK));
  }

}
