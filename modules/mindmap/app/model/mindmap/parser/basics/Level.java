package model.mindmap.parser.basics;

public class Level {

	public static String getLevelGerman(int i) {
		switch(i) {
		case 0:
			return "Titel";
		case 1:
			return "berschrift1";
		case 2:
			return "berschrift2";
		case 3:
			return "berschrift3";
		case 4:
			return "berschrift4";
		case 5:
			return "berschrift5";
		case 6:
			return "berschrift6";
		case 7:
			return "berschrift7";
		case 8:
			return "berschrift8";
		case 9:
			return "berschrift9";
		}
		return null;
	}
	
	public static String getLevelEnglish(int i) {
		switch(i) {
		case 0:
			return "Title";
		case 1:
			return "Heading1";
		case 2:
			return "Heading2";
		case 3:
			return "Heading3";
		case 4:
			return "Heading4";
		case 5:
			return "Heading5";
		case 6:
			return "Heading6";
		case 7:
			return "Heading7";
		case 8:
			return "Heading8";
		case 9:
			return "Heading9";
		}
		return null;
	}
	
	public static int getDepth(String level) {
		switch(level) {
		case ("Titel"):
			return 0;
		case "berschrift1":
			return 1;
		case "berschrift2":
			return 2;
		case "berschrift3":
			return 3;
		case "berschrift4":
			return 4;
		case "berschrift5":
			return 5;
		case "berschrift6":
			return 6;
		case "berschrift7":
			return 7;
		case "berschrift8":
			return 8;
		case "berschrift9":
			return 9;
		default:
			return -1;
		}
	}
}
