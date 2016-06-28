package model.html.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import model.html.task.Task.TaskKey;
import org.junit.Test;

public class TaskKeyTest {
  
  @Test
  public void test() {
    TaskKey key1_1 = new TaskKey(1, 1);
    TaskKey key1_2 = new TaskKey(1, 2);
    TaskKey key2_1 = new TaskKey(2, 1);
    TaskKey key1_1_new = new TaskKey(1, 1);

    assertFalse(key1_1.equals(null));
    assertFalse(key1_1.equals(new Object()));
    assertFalse(key1_1.equals(key1_2));
    assertFalse(key1_1.equals(key2_1));

    assertTrue(key1_1.equals(key1_1));
    assertTrue(key1_1.equals(key1_1_new));

    assertEquals(key1_1.hashCode(), 1001);
    assertEquals(key1_2.hashCode(), 2001);
  }

}
