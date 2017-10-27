package model.questions;

public enum QuestionType {
    CHOICE("Auswahlfrage"), FILLOUT("TODO!"), FREETEXT("Freitextfrage");

    public final String german;

    QuestionType(String theGerman) {
        german = theGerman;
    }
}
