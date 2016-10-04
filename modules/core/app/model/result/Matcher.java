package model.result;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public abstract class Matcher<T> {
  
  private BiPredicate<T, T> equalsTest;
  private BiFunction<T, T, Match<T>> matchedAction;
  
  private Predicate<T> filter = null;
  
  public Matcher(BiPredicate<T, T> theEqualsTest, BiFunction<T, T, Match<T>> theMatchedAction) {
    equalsTest = theEqualsTest;
    matchedAction = theMatchedAction;
  }
  
  public MatchingResult<T> match(List<T> firstCollection, List<T> secondCollection) {
    List<Match<T>> matches = new LinkedList<>();
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
  
  public void setFilter(Predicate<T> theFilter) {
    filter = theFilter;
  }
  
  protected abstract MatchingResult<T> instantiateMatch(List<Match<T>> matches, List<T> firstCollection,
      List<T> secondCollection);
  
}
