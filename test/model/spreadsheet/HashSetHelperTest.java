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

import model.spread.HashSetHelper;
import model.spread.RegExpHelper;

public class HashSetHelperTest {
  
  @Test
  public void newTest() {
    String string1 = "<main:formula>$G$2/$G$9</main:formula></main:cfRule></xml-fragment>";
    String string2 = "<main:formula>($H$2:$H$9)</main:formula></main:cfRule></xml-fragment>";
    string1 = RegExpHelper.getExcelCFFormulaList(string1);
    string2 = RegExpHelper.getExcelCFFormulaList(string2);
    assertThat(HashSetHelper.getDiffOfTwoFormulas(string1, string2),
        equalTo("Ein Operator ([/]) fehlt.Der Bereich [G9, G2] fehlt."));
  }
  
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
