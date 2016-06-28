package model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ClosingTagFailTest.class, CorrectorTest.class, EmptyXMLTest.class, MissingTag.class, NoRoot.class,
    WrongTagFailTest.class})
public class AllTests {
  
}
