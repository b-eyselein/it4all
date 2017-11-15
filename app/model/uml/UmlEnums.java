package model.uml;

import model.Enums.Selectable;

public abstract class UmlEnums {

    public enum UmlExPart implements Selectable<UmlExPart> {
        CLASS_SELECTION, DIAG_DRAWING_HELP, DIAG_DRAWING, ATTRS_METHS, FINISHED
    }

    public enum UmlClassType {
        CLASS("Klasse"), INTERFACE("Interface"), ABSTRACT("Abstrakte Klasse");

        public final String german;

        UmlClassType(String theGerman) {
            german = theGerman;
        }
    }

    public enum UmlMultiplicity implements Selectable<UmlMultiplicity> {

        SINGLE("1"), UNBOUND("*");

        public final String representant;

        UmlMultiplicity(String theRepresentant) {
            representant = theRepresentant;
        }

//        public static UmlMultiplicity getByString(String rep) {
//            if ("1".equals(rep) || "SINGLE".equals(rep))
//                return SINGLE;
//            else if ("*".equals(rep) || "UNBOUND".equals(rep))
//                return UNBOUND;
//            else throw new IllegalArgumentException("Value " + rep + " is not allowed for a UmlMultiplicity!");
//        }

    }

    public enum UmlAssociationType implements Selectable<UmlAssociationType> {
        ASSOCIATION("Assoziation"), AGGREGATION("Aggregation"), COMPOSITION("Komposition");

        public final String germanName;

        UmlAssociationType(String theGermanName) {
            germanName = theGermanName;

        }
    }
}