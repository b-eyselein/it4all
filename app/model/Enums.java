package model;

import play.twirl.api.Html;
import scala.Option;
import scala.Some;

public abstract class Enums {

    public interface Selectable<T extends Selectable<T>> {

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

        FAILURE("warning-sign"),
        SUCCESSFUL_MATCH("ok"),
        PARTIAL_MATCH("question-sign"),
        UNSUCCESSFUL_MATCH("exclamation-sign"),
        ONLY_USER("remove"),
        ONLY_SAMPLE("minus");

        public final String glyphicon;

        MatchType(String glyphiconEnd) {
            glyphicon = "glyphicon glyphicon-" + glyphiconEnd;
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

    public enum Mark implements Selectable<Mark> {

        VERY_GOOD(1), GOOD(2), NEUTRAL(3), BAD(4), VERY_BAD(5), NO_MARK(-1);

        public final int value;

        Mark(int theMark) {
            value = theMark;
        }

        public String display(EvaluatedAspect evaledAspect) {
            switch (this) {
                case VERY_GOOD:
                    return "Sehr " + evaledAspect.positive.toLowerCase();
                case GOOD:
                    return evaledAspect.positive;
                case NEUTRAL:
                    return evaledAspect.neutral;
                case BAD:
                    return evaledAspect.negative;
                case VERY_BAD:
                    return "Sehr " + evaledAspect.negative.toLowerCase();
                case NO_MARK:
                default:
                    return "Keine Angabe";
            }
        }


        public static Option<Mark> byString(String str) {
            try {
                return new Some<>(valueOf(str));
            } catch (Exception e) {
                // ==> None!
                return Option.apply(null);
            }
        }
    }

    public enum EvaluatedAspect implements Selectable<EvaluatedAspect> {

        USED("Wie oft haben Sie dieses Tool genutzt?", "Oft", "Manchmal", "Selten"),

        SENSE("Finden Sie dieses Tool sinnvoll?", "Sinnvoll", "Neutral", "Sinnlos"),

        USABILITY("Wie bewerten Sie die allgemeine Bedienbarkeit dieses Tools?", "Gut", "Neutral", "Schlecht"),

        STYLE_OF_FEEDBACK("Wie bewerten Sie die Gestaltung des Feedbacks dieses Tools?", "Gut", "Neutral", "Schlecht"),

        FAIRNESS_OF_FEEDBACK("Wie bewerten Sie die Fairness der Evaluation dieses Tools?", "Fair", "Neutral", "Unfair");

        public final String question;
        public final String positive;
        public final String neutral;
        public final String negative;

        EvaluatedAspect(String theQuestion, String thePositive, String theNeutral, String theNegative) {
            question = theQuestion;
            positive = thePositive;
            neutral = theNeutral;
            negative = theNegative;
        }

        public static Option<EvaluatedAspect> byString(String str) {
            try {
                return new Some<>(valueOf(str));
            } catch (Exception e) {
                // ==> None!
                return Option.apply(null);
            }
        }
    }

}