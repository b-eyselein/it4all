package model.core.result;

public enum SuccessType {

    FAILURE(0, "danger"), NONE(0, "danger"), PARTIALLY(1, "warning"), COMPLETE(2, "success");

    private int points;
    private String color;

    private SuccessType(int thePoints, String theColor) {
        points = thePoints;
        color = theColor;
    }

    public String getColor() {
        return color;
    }

    public int getPoints() {
        return points;
    }

}