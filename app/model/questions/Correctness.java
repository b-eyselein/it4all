package model.questions;

public enum Correctness {

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