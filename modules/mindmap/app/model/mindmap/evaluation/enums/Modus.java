package model.mindmap.evaluation.enums;

public enum Modus {

	MINIMAL, MAXIMAL, MINIMAL_MAXIMAL, NOT_IMMEDIATE;
	
	public static final Modus getModus(String modus) {
		switch (modus.toUpperCase()) {
		case "MINIMAL":
			return MINIMAL;
		case "MAXIMAL":
			return MAXIMAL;
		case "MINIMAL_MAXIMAL":
			return MINIMAL_MAXIMAL;
		case "NOT_IMMEDIATE":
			return NOT_IMMEDIATE;
		default:
			return null;
		}
	}
}
