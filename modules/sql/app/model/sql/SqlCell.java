package model.sql;

public class SqlCell {

  private final String content;
  private boolean different = false;

  public SqlCell(String theContent) {
    content = theContent;
  }

  @Override
  public boolean equals(Object object) {
    if(!(object instanceof SqlCell))
      return false;

    SqlCell other = (SqlCell) object;
    if(content == null && other.content == null)
      return true;

    return content != null && other.content != null && content.equals(other.content);
  }

  public String getContent() {
    return content;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((content == null) ? 0 : content.hashCode());
    return result;
  }

  public boolean isDifferent() {
    return different;
  }

  public void markAsDifferent() {
    different = true;
  }

}