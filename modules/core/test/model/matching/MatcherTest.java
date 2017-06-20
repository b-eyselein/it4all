package model.matching;

import java.util.Arrays;

import org.junit.Test;

public class MatcherTest {
  
  @Test
  public void testStringMatcher() {
    Matcher.STRING_EQ_MATCHER.match("", Arrays.asList("First List"), Arrays.asList("Second List"));
  }
  
}
