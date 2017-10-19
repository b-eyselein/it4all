package model.correctors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.base.Splitter;

import model.AvailableLanguages;
import model.DockerConnector;
import model.ProgEvaluationResult;
import model.testdata.ITestData;
import play.Logger;

public abstract class ProgLangCorrector {

    private static final Splitter NEWLINE_SPLITTER = Splitter.on("\n");

    private static final String PYTHON_SCRIPT_FILE = "sol.py";

    private static final String RESULT_FILE = "result.txt";

    private static final Set<PosixFilePermission> FILE_PERMS = new TreeSet<>(Arrays.asList(PosixFilePermission.OWNER_READ,
            PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE));

    private static final DockerConnector DOCKER_CONNECTOR = new DockerConnector();

    private static void createFile(Path file, List<String> content) throws IOException {
        if (file.toFile().exists())
            Files.delete(file);

        Files.write(file, content, StandardOpenOption.CREATE);
        Files.setPosixFilePermissions(file, FILE_PERMS);
    }

    // private static String getCommand() {
    // return "#!/bin/bash\n" + "rm result.txt\n" + "cat testdata.txt | while read
    // VAR do echo $VAR | python "
    // + PYTHON_SCRIPT_FILE + "; done > " + RESULT_FILE;
    // }

    public List<ProgEvaluationResult> evaluate(String learnerSolution, List<ITestData> completeTestData, Path solDir) {
        try {
            createFile(Paths.get(solDir.toString(), PYTHON_SCRIPT_FILE), NEWLINE_SPLITTER.splitToList(learnerSolution));

            Path shellScriptSource = Paths.get("conf", "resources", "prog", "python.sh");
            Path shellScriptTarget = Paths.get("/data", "solutions", "developer", "prog", "1", "script.sh");
            Files.copy(shellScriptSource, shellScriptTarget, StandardCopyOption.REPLACE_EXISTING);

            String ret = DOCKER_CONNECTOR.runContainer(AvailableLanguages.stdLang(), solDir, RESULT_FILE);

            Logger.debug("----------------------------------------------------------");
            Logger.debug("Returned:");
            Logger.debug(ret);
            Logger.debug("----------------------------------------------------------");

        } catch (IOException e) {
            Logger.error("Error ...", e);
        }
        return Collections.emptyList();
    }

    protected abstract boolean validateResult(Object realResult, Object awaitedResult);

}