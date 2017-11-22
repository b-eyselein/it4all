package model.programming

import java.nio.file.attribute.PosixFilePermission

import com.google.common.base.Splitter
import controllers.idExes.ProgToolObject
import model.User
import model.core.{CompleteResult, FileUtils}

object ProgLangCorrector {
  private val NEWLINE_SPLITTER: Splitter = Splitter.on("\n")

  val PYTHON_SCRIPT_FILE = "sol.py"

  val RESULT_FILE = "result.txt"

  val FILE_PERMS = new java.util.TreeSet[PosixFilePermission](java.util.Arrays.asList(PosixFilePermission.OWNER_READ,
    PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE))

  val filename = "solution_"

}

abstract class ProgLangCorrector extends FileUtils {

  def correct(user: User, exercise: ProgCompleteEx, learnerSolution: String, language: ProgLanguage): CompleteResult[ProgEvaluationResult] = {

    val imageExits = DockerConnector.imageExists(language.dockerImageName) || DockerConnector.pullImage(language.dockerImageName)

    val path = ProgToolObject.solutionDirForExercise(user.username, exercise.ex) / s"solution_${language.aceName}.${language.scriptEnding}"

    write(path, learnerSolution)

    println(path.toAbsolutePath)

    // TODO: save learnerSolution as file...
    //    Files.write(path, toWrite, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)

    // Check if image exists
    println(language.dockerImageName + " exists: " + imageExits)

    new CompleteResult(learnerSolution, Seq.empty)
  }

  //    private static void createFile(Path file, List<String> content) throws IOException {
  //        if (file.toFile().exists())
  //            Files.delete(file)
  //
  //        Files.write(file, content, StandardOpenOption.CREATE)
  //        Files.setPosixFilePermissions(file, FILE_PERMS)
  //    }

  // private static String getCommand() {
  // return "#!/bin/bash\n" + "rm result.txt\n" + "cat testdata.txt | while read
  // VAR do echo $VAR | python "
  // + PYTHON_SCRIPT_FILE + " done > " + RESULT_FILE
  // }

  //    public List<ProgEvaluationResult> evaluate(String learnerSolution /*, List<ITestData> completeTestData*/, Path solDir) {
  //        try {
  //            createFile(Paths.get(solDir.toString(), PYTHON_SCRIPT_FILE), NEWLINE_SPLITTER.splitToList(learnerSolution))
  //
  //            Path shellScriptSource = Paths.get("conf", "resources", "prog", "python.sh")
  //            Path shellScriptTarget = Paths.get("/data", "solutions", "developer", "prog", "1", "script.sh")
  //            Files.copy(shellScriptSource, shellScriptTarget, StandardCopyOption.REPLACE_EXISTING)
  //
  //            String ret = DOCKER_CONNECTOR.runContainer(AvailableLanguages.stdLang(), solDir, RESULT_FILE)
  //
  //            Logger.debug("----------------------------------------------------------")
  //            Logger.debug("Returned:")
  //            Logger.debug(ret)
  //            Logger.debug("----------------------------------------------------------")
  //
  //        } catch (IOException e) {
  //            Logger.error("Error ...", e)
  //        }
  //        return Collections.emptyList()
  //    }

  //    protected abstract boolean validateResult(Object realResult, Object awaitedResult)

}

object JavaCorrector extends ProgLangCorrector { // @Override
  // public String buildToEvaluate(String functionname, List<String> inputs) {
  // return functionname + "(" + String.join(", ", inputs) + ");";
  // }
  //    @Override
  //    protected boolean validateResult(Object realResult, Object awaitedResult) {
  //        // TODO Auto-generated method stub
  //        return false;
  //    }
}

object PythonCorrector extends ProgLangCorrector { // @Override
  // public String buildToEvaluate(String functionname, List<String> inputs) {
  // return functionname + "(" + String.join(", ", inputs) + ")";
  // }
  //    @Override
  //    protected boolean validateResult(Object realResult, Object awaitedResult) {
  //        // TODO Auto-generated method stub
  //        return false;
  //    }
}
