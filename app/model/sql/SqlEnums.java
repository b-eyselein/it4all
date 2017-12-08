package model.sql;

import model.Enums.Selectable;
import model.ExTag;
import scala.Option;
import scala.Some;


public abstract class SqlEnums {

    public enum SqlExerciseType implements Selectable<SqlExerciseType> {
        SELECT, CREATE, UPDATE, INSERT, DELETE;


        public static Option<SqlExerciseType> byString(String str) {
            try {
                return new Some<>(valueOf(str));
            } catch (Exception e) {
                // ==> None!
                return Option.apply(null);
            }
        }
    }

    public enum SqlExTag implements ExTag, Selectable<SqlExTag> {
        SQL_JOIN("J", "Join"),
        SQL_DOUBLE_JOIN("2J", "Zweifacher Join"),
        TRIPLE_JOIN("3J", "Dreifacher Join"),
        SQL_ORDER_BY("O", "Reihenfolge"),
        SQL_GROUP_BY("G", "Gruppierung"),
        SQL_FUNCTION("F", "Funktion"),
        SQL_ALIAS("A", "Alias"),
        SQL_LIMIT("L", "Limitierung"),
        SUBSELECT("S", "Zweites Select innerhalb");

        public final String buttonContent;
        public final String title;

        SqlExTag(String theButtonContent, String theTitle) {
            buttonContent = theButtonContent;
            title = theTitle;
        }

        @Override
        public String buttonContent() {
            return buttonContent;
        }

        @Override
        public String title() {
            return title;
        }

        public static Option<SqlExTag> byString(String str) {
            try {
                return new Some<>(valueOf(str));
            } catch (Exception e) {
                // ==> None!
                return Option.apply(null);
            }
        }
    }

}
