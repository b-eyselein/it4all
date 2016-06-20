package model;

import java.util.Arrays;
import java.util.List;

public class SqlExercise {
  
  public static List<SqlExercise> all() {
    return Arrays.asList(new SqlExercise(0), new SqlExercise(1));
  }

  public int id;

  public String text = "Geben Sie alle Telefonnummern aus!";

  public String sample = "SELECT phone FROM phonenumbers;";

  public SqlExercise(int theId) {
    id = theId;
  }

}
