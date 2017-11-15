package model.programming;

import model.Enums.Selectable;

public enum ProgLanguage implements Selectable<ProgLanguage> {

    PYTHON_3("Python 3", "python", "python:3", "sol.py", ProgConsts$.MODULE$.PYTHON_DEFAULT()),
    JAVA_8("Java 8", "java", "8-jdk", "sol.java", ProgConsts$.MODULE$.JAVA_DEFAULT());

    public static ProgLanguage STANDARD_LANG = PYTHON_3;

    public final String languageName;
    public final String aceName;
    public final String dockerImageName;
    public final String scriptName;
    public final String declaration;

    ProgLanguage(String theLanguageName, String theAceName, String theDockerImageName, String theScriptName, String theDeclaration) {
        languageName = theLanguageName;
        aceName = theAceName;
        dockerImageName = theDockerImageName;
        scriptName = theScriptName;
        declaration = theDeclaration;
    }

}
