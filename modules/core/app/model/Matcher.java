package model;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import model.exercise.EvaluationResult;
import model.exercise.Success;

public class Matcher<T> {

  public static class Match<T> extends EvaluationResult {
    private T arg1, arg2;

    public Match(T theArg1, T theArg2) {
      super(Success.NONE);
      arg1 = theArg1;
      arg2 = theArg2;
    }

    @Override
    public String getAsHtml() {
      // FIXME: implement result!
      return "<div class=\"alert alert-" + getBSClass() + "\">" + "TODO: IMPLEMENT! " + "</div>";
    }

    public T getFirstArg() {
      return arg1;
    }

    public T getSecondArg() {
      return arg2;
    }
  }

  private Comparator<T> comparator;
  private BiFunction<T, T, Match<T>> matchedAction;

  private List<Match<T>> matches = new LinkedList<>();
  private List<T> notMatchedInFirst, notMatchedInSecond;

  private Predicate<T> filter;

  public Matcher<T> comparator(Comparator<T> theComparator) {
    comparator = theComparator;
    return this;
  }

  public Matcher<T> filter(Predicate<T> theFilter) {
    filter = theFilter;
    return this;
  }

  public Matcher<T> firstCollection(List<T> theFirstCollection) {
    notMatchedInFirst = theFirstCollection;
    return this;
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

  public Matcher<T> match() {
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
    return this;
  }

  public Matcher<T> matchedAction(BiFunction<T, T, Match<T>> theMatchedAction) {
    matchedAction = theMatchedAction;
    return this;
  }

  public Matcher<T> secondCollection(List<T> theSecondCollection) {
    notMatchedInSecond = theSecondCollection;
    return this;
  }

}
