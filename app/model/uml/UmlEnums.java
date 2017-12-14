package model.uml;

import model.Enums.Selectable;
import scala.Option;
import scala.Some;

public abstract class UmlEnums {

    public enum UmlExPart implements Selectable<UmlExPart> {
        CLASS_SELECTION, DIAG_DRAWING_HELP, DIAG_DRAWING, ALLOCATION;

        public static Option<UmlExPart> byString(String str) {
            try {
                return new Some<>(valueOf(str));
            } catch (Exception e) {
                // ==> None!
                return Option.apply(null);
            }
        }

    }

    public enum UmlClassType {
        CLASS("Klasse"), INTERFACE("Interface"), ABSTRACT("Abstrakte Klasse");

        public final String german;

        UmlClassType(String theGerman) {
            german = theGerman;
        }

        public static Option<UmlClassType> byString(String str) {
            try {
                return new Some<>(valueOf(str));
            } catch (Exception e) {
                // ==> None!
                return Option.apply(null);
            }
        }

    }

    public enum UmlMultiplicity implements Selectable<UmlMultiplicity> {

        SINGLE("1"), UNBOUND("*");

        public final String representant;

        UmlMultiplicity(String theRepresentant) {
            representant = theRepresentant;
        }

        public static Option<UmlMultiplicity> byString(String str) {
            try {
                return new Some<>(valueOf(str));
            } catch (Exception e) {
                // ==> None!
                return Option.apply(null);
            }
        }

    }

    public enum UmlAssociationType implements Selectable<UmlAssociationType> {
        ASSOCIATION("Assoziation"), AGGREGATION("Aggregation"), COMPOSITION("Komposition");

        public final String germanName;

        UmlAssociationType(String theGermanName) {
            germanName = theGermanName;
        }

        public static Option<UmlAssociationType> byString(String str) {
            try {
                return new Some<>(valueOf(str));
            } catch (Exception e) {
                // ==> None!
                return Option.apply(null);
            }
        }

    }
}