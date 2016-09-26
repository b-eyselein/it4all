package model.result;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class Matcher<T> {

  private Comparator<T> comparator;
  private BiFunction<T, T, Match<T>> matchedAction;
  
  private List<Match<T>> matches = new LinkedList<>();
  private List<T> notMatchedInFirst, notMatchedInSecond;
  
  private Predicate<T> filter;
  
  public Matcher(List<T> theFirstCollection, List<T> theSecondCollection) {
    notMatchedInFirst = new ArrayList<>(theFirstCollection);
    notMatchedInSecond = new ArrayList<>(theSecondCollection);
  }

  public List<Match<T>> getMatches() {
    return matches;
  }
  
  public List<T> getNotMatchedInFirst() {
    return notMatchedInFirst;
  }
  
  public List<T> getNotMatchedInSecond() {
    return notMatchedInSecond;
  }
  
  public void match() {
    for(Iterator<T> iter1 = notMatchedInFirst.iterator(); iter1.hasNext();) {
      T arg1 = iter1.next();
      
      if(filter != null && !filter.test(arg1))
        continue;
      
      Iterator<T> iter2 = notMatchedInSecond.iterator();
      boolean matched = false;
      
      while(iter2.hasNext() && !matched) {
        T arg2 = iter2.next();
        
        if(filter != null && !filter.test(arg2))
          continue;
        
        if(comparator.compare(arg1, arg2) == 0) {
          matches.add(matchedAction.apply(arg1, arg2));
          iter1.remove();
          iter2.remove();
          matched = true;
        }
      }
    }
  }
  
  public void setComparator(Comparator<T> theComparator) {
    comparator = theComparator;
  }
  
  public void setFilter(Predicate<T> theFilter) {
    filter = theFilter;
  }
  
  public void setMatchedAction(BiFunction<T, T, Match<T>> theMatchedAction) {
    matchedAction = theMatchedAction;
  }
  
}
