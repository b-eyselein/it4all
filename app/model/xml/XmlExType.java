package model.xml;

import model.Enums.Selectable;
import model.ExTag;
import scala.Option;
import scala.Some;

public enum XmlExType implements Selectable<XmlExType>, ExTag {

    XML_XSD("XML", "XSD", "xml", "xsd", "xsd"),

    XML_DTD("XML", "DTD", "xml", "dtd", "dtd"),

    XSD_XML("XSD", "XML", "xsd", "xml", "xsd"),

    DTD_XML("DTD", "XML", "dtd", "xml", "dtd");

    public final String given;
    public final String toCreate;

    public final String studFileEnding;
    public final String refFileEnding;
    public final String gramFileEnding;

    XmlExType(String theGiven, String theToCreate, String theStudFileEnding, String theRefFileEnding, String theGramFileEnding) {
        given = theGiven;
        toCreate = theToCreate;
        studFileEnding = theStudFileEnding;
        refFileEnding = theRefFileEnding;
        gramFileEnding = theGramFileEnding;
    }

    @Override
    public String buttonContent() {
        return given + "_" + toCreate;
    }

    @Override
    public String title() {
        return toCreate + " gegen " + given;
    }

    public static Option<XmlExType> byString(String str) {
        try {
            return new Some<>(valueOf(str));
        } catch (Exception e) {
            // ==> None!
            return Option.apply(null);
        }
    }

}
