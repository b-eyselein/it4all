package model.web;

import model.Enums.Selectable;
import scala.Option;
import scala.Some;

public abstract class WebEnums {


    public enum JsActionType implements Selectable<JsActionType> {

        CLICK, FILLOUT;

        public static Option<JsActionType> byString(String str) {
            try {
                return new Some<>(valueOf(str));
            } catch (Exception e) {
                // ==> None!
                return Option.apply(null);
            }
        }

    }

    public enum WebExPart implements Selectable<WebExPart> {

        HTML_PART("html"),
        JS_PART("js");

        public final String shortName;

        WebExPart(String theShortName) {
            shortName = theShortName;
        }

        public static Option<WebExPart> byShortName(String str) {
            if (HTML_PART.shortName.equals(str))
                return new Some<>(HTML_PART);
            else if (JS_PART.shortName.equals(str))
                return new Some<>(JS_PART);
            else return Option.apply(null);
        }

        public static Option<WebExPart> byString(String str) {
            try {
                return new Some<>(valueOf(str));
            } catch (Exception e) {
                // ==> None!
                return Option.apply(null);
            }
        }
    }

}