package model.matching;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Matcher<T> {
  
  public static class StringEqualsMatcher extends Matcher<String> {

    public StringEqualsMatcher() {
      super(String::equals);
    }
    
  }
  
  private BiPredicate<T, T> equalsTest;
  
  private Predicate<T> filter = null;
  
  public Matcher(BiPredicate<T, T> theEqualsTest) {
    equalsTest = theEqualsTest;
  }
  
  public MatchingResult<T> match(List<T> firstCollection, List<T> secondCollection) {
    List<Match<T>> matches = new LinkedList<>();
    
    // Copy lists to prevent change in real lists
    List<T> firstList = new LinkedList<>(firstCollection);
    List<T> secondList = new LinkedList<>(secondCollection);
    
    // FIXME: boolean for ordered matching?
    
    for(Iterator<T> iter1 = firstList.iterator(); iter1.hasNext();) {
      T arg1 = iter1.next();
      
      if(filter != null && !filter.test(arg1))
        continue;
      
      Iterator<T> iter2 = secondList.iterator();
      boolean matched = false;
      
      while(iter2.hasNext() && !matched) {
        T arg2 = iter2.next();
        
        if(filter != null && !filter.test(arg2))
          continue;
        
        if(equalsTest.test(arg1, arg2)) {
          
          matches.add(new Match<>(arg1, arg2));
          iter1.remove();
          iter2.remove();
          matched = true;
        }
      }
    }
    
    return new MatchingResult<>(matches, firstCollection, secondCollection);
  }
  
  public MatchingResult<T> matchInOrder(List<T> firstCollection, List<T> secondCollection) {
    List<Match<T>> matches = new LinkedList<>();
    
    Iterator<T> iter1 = firstCollection.iterator();
    Iterator<T> iter2 = secondCollection.iterator();
    
    while(iter1.hasNext() && iter2.hasNext()) {
      T arg1 = iter1.next();
      T arg2 = iter2.next();
      matches.add(new Match<>(arg1, arg2));
      iter1.remove();
      iter2.remove();
    }
    
    return new MatchingResult<>(matches, firstCollection, secondCollection);
  }
  
  public void setFilter(Predicate<T> theFilter) {
    filter = theFilter;
  }
}
