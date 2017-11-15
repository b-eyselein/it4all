package model.questions;

import model.Enums.Selectable;

public class QuestionEnums {

    public enum Correctness implements Selectable<Correctness> {

        CORRECT("Korrekt", "glyphicon glyphicon-ok-sign", "success"),
        OPTIONAL("Optional", "glyphicon glyphicon-question-sign", "warning"),
        WRONG("Falsch", "glyphicon glyphicon-remove-sign", "danger");

        public final String title;
        public final String bsGlyphicon;
        public final String bsButtonColor;

        Correctness(String theTitle, String theBSGlyphicon, String theBSButtonColor) {
            title = theTitle;
            bsGlyphicon = theBSGlyphicon;
            bsButtonColor = theBSButtonColor;
        }

    }

    public enum QuestionType {
        CHOICE("Auswahlfrage"), FILLOUT("TODO!"), FREETEXT("Freitextfrage");

        public final String german;

        QuestionType(String theGerman) {
            german = theGerman;
        }
    }


}
