package model.spreadsheet;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class SpreadSheetCorrectionResultTest {
  
  @Test
  public void testIsSuccess() {
    SpreadSheetCorrectionResult result = new SpreadSheetCorrectionResult(true, Collections.emptyList());
    assertTrue(result.isSuccess());
    
    result = new SpreadSheetCorrectionResult(false, Collections.emptyList());
    assertFalse(result.isSuccess());
  }
  
  @Test
  public void testGetNotices() {
    List<String> notices = Arrays.asList("Notice 1...", "Notice 2...");
    SpreadSheetCorrectionResult result = new SpreadSheetCorrectionResult(false, notices);
    assertThat(result.getNotices(), equalTo(notices));
    assertThat(result.getNotices().size(), equalTo(2));
    assertThat(result.getNotices().get(0), equalTo(notices.get(0)));
    assertThat(result.getNotices().get(1), equalTo(notices.get(1)));
  }
}