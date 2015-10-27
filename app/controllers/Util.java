package controllers;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Util {

	public static Path getSolDirForUser(String user) {
		return Paths.get("/var/lib/it4all/" + user + "/");
	}
	
	public static Path getSolDirForUserAndType(String type, String user) {
		return Paths.get(getSolDirForUser(user).toString(), type + "/");
	}
	
	public static Path getSolFileForExercise(String user, int exercise) {
		//TODO: Test for Html!
		return Paths.get(getSolDirForUserAndType("html", user).toString(), exercise + ".html");
	}
	
}
