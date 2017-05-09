package model.question;

public enum QuestionType {

  CHOICE("Auswahlfrage"), FILLOUT("Ausfüllfrage"), FREETEXT("Freitextfrage");

  private String name;

  private QuestionType(String theName) {
    name = theName;
  }

  public String getName() {
    return name;
  }

}
