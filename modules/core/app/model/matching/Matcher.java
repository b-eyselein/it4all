package model.matching;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public abstract class Matcher<T, M extends Match<T>> {
  
  public static final Matcher<String, Match<String>> STRING_EQ_MATCHER = new Matcher<String, Match<String>>(
      String::equals) {
    
    @Override
    protected Match<String> instantiateMatch(String arg1, String arg2) {
      return new GenericMatch<>(arg1, arg2);
    }
    
  };
  
  private BiPredicate<T, T> equalsTest;
  
  private Predicate<T> filter = null;
  
  public Matcher(BiPredicate<T, T> theEqualsTest) {
    equalsTest = theEqualsTest;
  }
  
  public MatchingResult<T, M> match(String matchName, List<T> firstCollection, List<T> secondCollection) {
    List<M> matches = new LinkedList<>();
    
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
          matches.add(instantiateMatch(arg1, arg2));
          iter1.remove();
          iter2.remove();
          matched = true;
        }
      }
    }
    
    return new MatchingResult<>(matchName, matches, firstList, secondList);
  }
  
  public void setFilter(Predicate<T> theFilter) {
    filter = theFilter;
  }
  
  protected abstract M instantiateMatch(T arg1, T arg2);
}
