package model.matching;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class Matcher<T, M extends Match<T>> {
  
  private final String matchName;
  
  private final BiPredicate<T, T> equalsTest;
  
  private final BiFunction<T, T, M> matchInstantiation;
  
  public Matcher(String theMatchName, BiPredicate<T, T> theEqualsTest, BiFunction<T, T, M> theMatchInstantiation) {
    matchName = theMatchName;
    equalsTest = theEqualsTest;
    matchInstantiation = theMatchInstantiation;
  }
  
  public MatchingResult<T, M> doMatch(List<T> firstCollection, List<T> secondCollection) {
    // "match" is a reserved keyword in scala...
    return match(firstCollection, secondCollection);
  }
  
  public MatchingResult<T, M> match(List<? extends T> firstCollection, List<? extends T> secondCollection) {
    List<M> matches = new LinkedList<>();
    
    // Copy lists to prevent change in real lists
    List<T> firstList = new LinkedList<>(firstCollection);
    List<T> secondList = new LinkedList<>(secondCollection);
    
    for(Iterator<T> iter1 = firstList.iterator(); iter1.hasNext();) {
      T arg1 = iter1.next();
      
      Iterator<T> iter2 = secondList.iterator();
      boolean matched = false;
      
      while(iter2.hasNext() && !matched) {
        T arg2 = iter2.next();
        
        if(equalsTest.test(arg1, arg2)) {
          matches.add(matchInstantiation.apply(arg1, arg2));
          iter1.remove();
          iter2.remove();
          matched = true;
        }
      }
    }
    
    List<M> wrong = firstList.stream().map(t -> matchInstantiation.apply(t, null)).collect(Collectors.toList());
    List<M> missing = secondList.stream().map(t -> matchInstantiation.apply(null, t)).collect(Collectors.toList());
    
    matches.addAll(wrong);
    matches.addAll(missing);
    
    return new MatchingResult<>(matchName, matches);
  }
  
}
