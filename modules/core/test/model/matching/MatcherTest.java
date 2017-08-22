package model.matching;

import java.util.Arrays;

import org.junit.Test;

public class MatcherTest {
  
  @Test
  public void testStringMatcher() {
    new StringEqualsMatcher("TestString").match(Arrays.asList("First List"), Arrays.asList("Second List"));
  }
  
}
