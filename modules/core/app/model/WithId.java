package model;

public interface WithId extends Comparable<WithId> {

  @Override
  public default int compareTo(WithId other) {
    return getId() - other.getId();
  }

  public int getId();
  
}
