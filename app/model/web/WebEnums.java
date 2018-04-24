package model.web;

import model.Enums.Selectable;
import scala.Option;
import scala.Some;

public abstract class WebEnums {

    // FIXME: Use enumeratum!

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


}