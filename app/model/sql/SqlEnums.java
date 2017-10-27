package model.sql;

import model.Enums;
import model.core.ExTag;

public abstract class SqlEnums {

    public enum SqlExerciseType implements Enums.Selectable<SqlExerciseType> {

        SELECT, CREATE, UPDATE, INSERT, DELETE;

    }

    public enum SqlExTag implements ExTag {
        JOIN("J", "Join"),
        DOUBLE_JOIN("2J", "Zweifacher Join"),
        TRIPLE_JOIN("3J", "Dreifacher Join"),
        ORDER_BY("O", "Reihenfolge"),
        GROUP_BY("G", "Gruppierung"),
        FUNCTION("F", "Funktion"),
        ALIAS("A", "Alias"),
        LIMIT("L", "Limitierung"),
        SUBSELECT("S", "Zweites Select innerhalb");

        private final String buttonContent;
        private final String title;

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

    }

}
