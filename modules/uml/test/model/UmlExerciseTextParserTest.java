package model;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

public class UmlExerciseTextParserTest {

  @Test
    public void testReadSimpleReplacements() {
      List<String> capWords = UmlExerciseTextParser.parseExText();
  
      capWords.forEach(System.out::println);
  
      fail("Not yet implemented");
    }

}
