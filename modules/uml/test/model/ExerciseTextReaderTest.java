package model;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

public class ExerciseTextReaderTest {

  @Test
  public void testParseExText() {
    List<String> capWords = ExerciseTextReader.parseExText();

    capWords.forEach(System.out::println);

    fail("Not yet implemented");
  }

}
