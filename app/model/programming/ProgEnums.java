package model.programming;

import controllers.exes.idPartExes.AIdPartExController;
import model.Enums;
import model.essentials.TRUE;
import play.api.libs.json.JsBoolean;
import play.api.libs.json.JsNumber;
import play.api.libs.json.JsString;
import play.api.libs.json.JsValue;
import scala.Option;
import scala.Some;
import scala.math.BigDecimal;

import java.util.Arrays;
import java.util.List;

public abstract class ProgEnums {

    public enum ProgExPart implements Enums.Selectable<ProgExPart>, AIdPartExController.ExPart {

        TESTDATA_CREATION("Erstellen der Testdaten"),
        IMPLEMENTATION("Implementierung");

        public final String partName;

        ProgExPart(String thePartName) {
            partName = thePartName;
        }

        public static Option<ProgExPart> byString(String str) {
            try {
                return new Some<>(valueOf(str));
            } catch (Exception e) {
                // ==> None!
                return Option.apply(null);
            }
        }

    }

    public enum DataTypes implements Enums.Selectable<DataTypes> {


        INTEGER("int"),
        FLOAT("float"),
        BOOLEAN("boolean"),
        STRING("string");

        private static List<String> TRUE_VALUES = Arrays.asList("true", "1");

        public final String typeName;

        DataTypes(String theTypeName) {
            typeName = theTypeName;
        }

        public static Option<DataTypes> byName(String str) {
            for (DataTypes value : values())
                if (value.typeName.equals(str))
                    return new Some<>(value);

            return Option.apply(null);
        }

        public JsValue toJson(String data) {
            switch (this) {
                case INTEGER:
                    return new JsNumber(BigDecimal.decimal(Integer.parseInt(data)));
                case FLOAT:
                    return new JsNumber(BigDecimal.decimal(Double.parseDouble(data)));
                case BOOLEAN:
                    return JsBoolean.apply(TRUE_VALUES.contains(data));
                case STRING:
                default:
                    return new JsString(data);
            }
        }

        public static Option<DataTypes> byString(String str) {
            try {
                return new Some<>(valueOf(str));
            } catch (Exception e) {
                // ==> None!
                return Option.apply(null);
            }
        }
    }
}
