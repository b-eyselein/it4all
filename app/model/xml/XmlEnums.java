package model.xml;

import scala.Option;
import scala.Some;

public abstract class XmlEnums {

    public enum XmlErrorType  {
        FATAL("Fataler Fehler"),
        ERROR("Fehler"),
        WARNING("Warnung"),
        FAILURE("Fehlschlag");

        public final String german;

        XmlErrorType(String theGerman) {
            german = theGerman;
        }

        public static Option<XmlErrorType> byString(String str) {
            try {
                return new Some<>(valueOf(str));
            } catch (Exception e) {
                // ==> None!
                return Option.apply(null);
            }
        }

    }

}
