package model.questions;

import model.Enums.Selectable;
import scala.Option;
import scala.Some;

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

        public static Option<Correctness> byString(String str) {
            try {
                return new Some<>(valueOf(str));
            } catch (Exception e) {
                // ==> None!
                return Option.apply(null);
            }
        }

    }

    public enum QuestionType {
        CHOICE("Auswahlfrage"),
        //        FILLOUT("TODO!"),
        FREETEXT("Freitextfrage");

        public final String german;

        QuestionType(String theGerman) {
            german = theGerman;
        }

        public static Option<QuestionType> byString(String str) {
            try {
                return new Some<>(valueOf(str));
            } catch (Exception e) {
                // ==> None!
                return Option.apply(null);
            }
        }

    }


}
