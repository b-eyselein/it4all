package model;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

public class Matcher<T> {

  private TreeSet<T> firstCollection;
  private TreeSet<T> secondCollection;
  // private Comparator<T> comparator;

  public Matcher(Collection<T> theFirstCollection, Collection<T> theSecondCollection, Comparator<T> theComparator) {
    System.out.println("Before: " + theFirstCollection + ", " + theSecondCollection);
    firstCollection = new TreeSet<>(theComparator);
    firstCollection.addAll(theFirstCollection);

    secondCollection = new TreeSet<>(theComparator);
    secondCollection.addAll(theSecondCollection);

    // comparator = theComparator;
    match();
  }

  private void match() {
    System.out.println("First: " + firstCollection);
    System.out.println("Second: " + secondCollection);
  }

}
