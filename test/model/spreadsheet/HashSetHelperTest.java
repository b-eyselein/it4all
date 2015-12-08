package model.spreadsheet;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.junit.Test;

public class HashSetHelperTest {
  
  @Test
  public void testGetDifferenceOfCollections() {
    HashSet<String> strings1 = new HashSet<String>(Arrays.asList("Hallo", "dies", "ist", "ein", "Test"));
    HashSet<String> strings2 = new HashSet<String>(Arrays.asList("Hallo", "das", "ist", "ein", "schlechter", "Test"));
    
    Collection<String> diff = HashSetHelper.getDifferenceOfCollections(strings1, strings2);
    Iterator<String> iter = diff.iterator();
    assertTrue(iter.hasNext());
    assertThat(iter.next(), equalTo("dies"));
    assertFalse(iter.hasNext());
    
    diff = HashSetHelper.getDifferenceOfCollections(strings2, strings1);
    iter = diff.iterator();
    assertTrue(iter.hasNext());
    assertThat(iter.next(), equalTo("schlechter"));
    assertTrue(iter.hasNext());
    assertThat(iter.next(), equalTo("das"));
    assertFalse(iter.hasNext());
  }
  
  @Test
  public void testGetSheetCFDiff() {
    // fail("Not yet implemented");
  }
  
}
