package model.matching;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public abstract class Matcher<T, U extends Match<T>> {

  private BiPredicate<T, T> equalsTest;
  private BiFunction<T, T, U> matchedAction;

  private Predicate<T> filter = null;

  public Matcher(BiPredicate<T, T> theEqualsTest, BiFunction<T, T, U> theMatchedAction) {
    equalsTest = theEqualsTest;
    matchedAction = theMatchedAction;
  }

  public MatchingResult<T, U> match(List<T> firstCollection, List<T> secondCollection) {
    List<U> matches = new LinkedList<>();

    // FIXME: boolean for ordered matching?

    for(Iterator<T> iter1 = firstCollection.iterator(); iter1.hasNext();) {
      T arg1 = iter1.next();

      if(filter != null && !filter.test(arg1))
        continue;

      Iterator<T> iter2 = secondCollection.iterator();
      boolean matched = false;

      while(iter2.hasNext() && !matched) {
        T arg2 = iter2.next();

        if(filter != null && !filter.test(arg2))
          continue;

        if(equalsTest.test(arg1, arg2)) {
          matches.add(matchedAction.apply(arg1, arg2));
          iter1.remove();
          iter2.remove();
          matched = true;
        }
      }
    }

    return instantiateMatch(matches, firstCollection, secondCollection);
  }

  public MatchingResult<T, U> matchInOrder(List<T> firstCollection, List<T> secondCollection) {
    List<U> matches = new LinkedList<>();

    Iterator<T> iter1 = firstCollection.iterator();
    Iterator<T> iter2 = secondCollection.iterator();

    while(iter1.hasNext() && iter2.hasNext()) {
      T arg1 = iter1.next(), arg2 = iter2.next();
      matches.add(matchedAction.apply(arg1, arg2));
      iter1.remove();
      iter2.remove();
    }

    return instantiateMatch(matches, firstCollection, secondCollection);
  }

  public void setFilter(Predicate<T> theFilter) {
    filter = theFilter;
  }

  protected abstract MatchingResult<T, U> instantiateMatch(List<U> matches, List<T> firstCollection,
      List<T> secondCollection);

}
