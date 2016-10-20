package model.mindmap.evaluation;

import java.util.List;
import java.util.Map;

public class TupelDifferenceContainer {

  private Map<String, List<Tuple>> mapMissing;
  private Map<String, List<Tuple>> mapWrong;
  private Map<String, List<Tuple>> mapPartiallyCorrect;

  public TupelDifferenceContainer(Map<String, List<Tuple>> mapMissing, Map<String, List<Tuple>> mapWrong,
      Map<String, List<Tuple>> mapPartiallyCorrect) {
    super();
    this.mapMissing = mapMissing;
    this.mapWrong = mapWrong;
    this.mapPartiallyCorrect = mapPartiallyCorrect;
  }

  public Map<String, List<Tuple>> getMapMissing() {
    return mapMissing;
  }

  public Map<String, List<Tuple>> getMapPartiallyCorrect() {
    return mapPartiallyCorrect;
  }

  public Map<String, List<Tuple>> getMapWrong() {
    return mapWrong;
  }

}
