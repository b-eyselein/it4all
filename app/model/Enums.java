package model;

import play.twirl.api.Html;

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
        RESERVED, CREATED, ACCEPTED, APPROVED
    }

    public enum Role implements Selectable<Role> {
        RoleUser, RoleAdmin, RoleSuperAdmin
    }

    public enum ShowHideAggregate implements Selectable<ShowHideAggregate> {
        SHOW("Einblenden"), HIDE("Ausblenden"), AGGR("Zusammenfassen");

        public final String german;

        ShowHideAggregate(String theGerman) {
            german = theGerman;
        }
    }

    public enum ToolState implements Selectable<ToolState> {
        LIVE("Verfügbare Tools", Role.RoleUser), ALPHA("Tools in Alpha-Status", Role.RoleAdmin), BETA("Tools in Beta-Status", Role.RoleAdmin);

        public final String german;
        public final Role requiredRole;

        ToolState(String theGerman, Role theRequiredRole) {
            german = theGerman;
            requiredRole = theRequiredRole;
        }

        public Html badge() {
            return new Html(this == LIVE ? "" : "<sup>" + name() + "</sup>");
        }
    }

    public enum FeedbackLevel implements Selectable<FeedbackLevel> {
        NO_FB("Kein Feedback"), MI_FB("Minimales Feedback"), ME_FB("Mittleres Feedback"), FU_BF("Volles Feedback");

        public final String description;

        FeedbackLevel(String theDescription) {
            description = theDescription;
        }
    }

    public enum MatchType implements Selectable<MatchType> {

        FAILURE("exclamation-sign"),
        SUCCESSFUL_MATCH("ok"),
        UNSUCCESSFUL_MATCH("question-sign"),
        ONLY_USER("remove"),
        ONLY_SAMPLE("minus");

        public final String glyphicon;

        MatchType(String glyphiconEnd) {
            glyphicon = "glyphicon glyphicon-" + glyphiconEnd;
        }
    }


    public enum SuccessType {

        FAILURE(0, "danger"), NONE(0, "danger"), PARTIALLY(1, "warning"), COMPLETE(2, "success");

        public final int points;
        public final String color;

        SuccessType(int thePoints, String theColor) {
            points = thePoints;
            color = theColor;
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

    }

}