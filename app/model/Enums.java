package model;

import play.twirl.api.Html;
import scala.Option;
import scala.Some;

public abstract class Enums {

    // FIXME: ==> enumeratum.Enum!

    public static interface Selectable<T extends Selectable<T>> {

        default String isSelected(T that) {
            return this == that ? "selected" : "";
        }

        default String isChecked(T that) {
            return this == that ? "checked" : "";
        }
    }

    public enum ExerciseState implements Selectable<ExerciseState> {
        RESERVED, CREATED, ACCEPTED, APPROVED;

        public static Option<ExerciseState> byString(String str) {
            try {
                return new Some<>(valueOf(str));
            } catch (Exception e) {
                // ==> None!
                return Option.apply(null);
            }
        }
    }

    public enum Role implements Selectable<Role> {
        RoleUser, RoleAdmin, RoleSuperAdmin;

        public static Option<Role> byString(String str) {
            try {
                return new Some<>(valueOf(str));
            } catch (Exception e) {
                // ==> None!
                return Option.apply(null);
            }
        }
    }

    public enum ShowHideAggregate implements Selectable<ShowHideAggregate> {
        SHOW("Einblenden"), HIDE("Ausblenden"), AGGR("Zusammenfassen");

        public final String german;

        ShowHideAggregate(String theGerman) {
            german = theGerman;
        }

        public static Option<ShowHideAggregate> byString(String str) {
            try {
                return new Some<>(valueOf(str));
            } catch (Exception e) {
                // ==> None!
                return Option.apply(null);
            }
        }
    }

    public enum ToolState implements Selectable<ToolState> {
        LIVE("Verfügbare Tools", "", Role.RoleUser),
        ALPHA("Tools in Alpha-Status", "&alpha;", Role.RoleAdmin),
        BETA("Tools in Beta-Status", "&beta;", Role.RoleAdmin);

        public final String german;
        public final String greek;
        public final Role requiredRole;

        ToolState(String theGerman, String theGreek, Role theRequiredRole) {
            german = theGerman;
            greek = theGreek;
            requiredRole = theRequiredRole;
        }

        public Html badge() {
            return new Html(this == LIVE ? "" : "<sup>" + greek + "</sup>");
        }

        public static Option<ToolState> byString(String str) {
            try {
                return new Some<>(valueOf(str));
            } catch (Exception e) {
                // ==> None!
                return Option.apply(null);
            }
        }
    }

    public enum FeedbackLevel implements Selectable<FeedbackLevel> {
        NO_FB("Kein Feedback"), MI_FB("Minimales Feedback"), ME_FB("Mittleres Feedback"), FU_BF("Volles Feedback");

        public final String description;

        FeedbackLevel(String theDescription) {
            description = theDescription;
        }

        public static Option<FeedbackLevel> byString(String str) {
            try {
                return new Some<>(valueOf(str));
            } catch (Exception e) {
                // ==> None!
                return Option.apply(null);
            }
        }
    }

    public enum MatchType implements Selectable<MatchType> {

        FAILURE("warning-sign", "danger"),

        SUCCESSFUL_MATCH("ok", "success"),
        PARTIAL_MATCH("question-sign", "warning"),
        UNSUCCESSFUL_MATCH("exclamation-sign", "danger"),

        ONLY_USER("remove", "danger"),
        ONLY_SAMPLE("minus", "danger");

        public final String glyphicon;
        public final String bsClass;

        MatchType(String glyphiconEnd, String bsClass) {
            this.glyphicon = "glyphicon glyphicon-" + glyphiconEnd;
            this.bsClass = bsClass;
        }

        public static Option<MatchType> byString(String str) {
            try {
                return new Some<>(valueOf(str));
            } catch (Exception e) {
                // ==> None!
                return Option.apply(null);
            }
        }
    }

    public enum SuccessType implements Selectable<SuccessType> {

        ERROR(0, "danger", "glyphicon glyphicon-remove"),
        NONE(0, "danger", "glyphicon glyphicon-remove"),
        PARTIALLY(1, "warning", "glyphicon glyphicon-question-sign"),
        COMPLETE(2, "success", "glyphicon glyphicon-ok");

        public final int points;
        public final String color;
        public final String glyphicon;

        SuccessType(int thePoints, String theColor, String theGlyphicon) {
            points = thePoints;
            color = theColor;
            glyphicon = theGlyphicon;
        }

        public static SuccessType ofBool(boolean success) {
            return success ? COMPLETE : NONE;
        }

        public static Option<SuccessType> byString(String str) {
            try {
                return new Some<>(valueOf(str));
            } catch (Exception e) {
                // ==> None!
                return Option.apply(null);
            }
        }
    }

}