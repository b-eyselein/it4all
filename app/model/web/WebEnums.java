package model.web;

import scala.Option;
import scala.Some;

public abstract class WebEnums {

    // FIXME: Use enumeratum!

    public enum JsActionType {

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


}