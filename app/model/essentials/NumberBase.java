package model.essentials;

import model.Enums.Selectable;
import play.twirl.api.Html;

public enum NumberBase implements Selectable<NumberBase> {

    BINARY(2, "Binär", "0b", "0-1", "0-1"),
    OCTAL(8, "Oktal", "0o", "1-7", "0-7"),
    HEXADECIMAL(16, "Hexadezimal", "0x", "1-9a-fA-F", "0-9a-fA-F"),
    DECIMAL(10, "Dezimal", "", "1-9", "0-9");

    public final int base;
    public final String baseName;
    public final String mark;
    public final String regexStart;
    public final String regexRest;

    NumberBase(int theBase, String theBaseName, String theMark, String theRegexStart, String theRegexRest) {
        base = theBase;
        baseName = theBaseName;
        mark = theMark;
        regexStart = theRegexStart;
        regexRest = theRegexRest;
    }

    public final String htmlPattern() {
        return "[\\s" + regexRest + "]+";
    }

    public final String pluralName() {
        return baseName + "zahlen";
    }

    public final String regex() {
        return "-?" + mark + "[" + regexStart + "][" + regexRest + "]*";
    }

    public final String singularName() {
        return baseName + "zahl";
    }

    public final String systemName() {
        return baseName + "system";
    }

    public final Html dispBase() {
        return new Html("<sub>" + (base < 10 ? "&nbsp; " : "") + base + "</sub>");
    }

}