package model.html.result;

public enum Success {
  COMPLETE("+"), PARTIALLY("o"), NONE("-");

  private String jsonRepresentant;

  private Success(String jsonRep) {
    jsonRepresentant = jsonRep;
  }

  public String getJsonRepresentant() {
    return this.jsonRepresentant;
  }

}