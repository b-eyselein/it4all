package model;

public class ColumnWrapper<T> {

  private T column;

  public ColumnWrapper(T theColumn) {
    column = theColumn;
  }

  public T getColumn() {
    return column;
  }

}
