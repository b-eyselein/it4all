package model.programming

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.core.DockerClientBuilder
import com.github.dockerjava.core.command.PullImageResultCallback

import scala.collection.JavaConverters._

object DockerConnector {

  //    private static final String COMMAND = "bash" // "./script.sh"

  private val DOCKER_CLIENT: DockerClient = DockerClientBuilder.getInstance().build()

  def info() = println(DOCKER_CLIENT.infoCmd().exec())

  //    private static boolean checkImage(String image) {
  //        return imageExists(image) || pullImage(IMAGE_PYTHON)
  //    }

  //    private static String createContainer(String image, Path workingDir) {
  //        Path scriptFile = Paths.get("conf", "resources", "prog", "python.sh").toAbsolutePath()
  //        System.out.println(scriptFile.toString() + " :: " + scriptFile.toFile().exists())
  //        Bind scriptBind = new Bind(scriptFile.toString(), new Volume("/data/solutions/developer/prog/1/script.sh"))
  //
  //        return DOCKER_CLIENT
  //                .createContainerCmd(image)
  //                .withName(CONTAINER_NAME)
  //                .withVolumesFrom(new VolumesFrom("it4all_it4all_1"))
  //                .withBinds(scriptBind)
  //                .withWorkingDir(workingDir.toString())
  //                .withCmd(COMMAND)
  //                .withTty(true)
  //                .withStdinOpen(true)
  //                .exec()
  //                .getId()
  //    }

  //    private static String getContainerID(String name) {
  //        return DOCKER_CLIENT
  //                .listContainersCmd().withShowAll(true) // ps -a
  //                .exec()
  //                .stream()
  //                .filter(container -> Arrays.asList(container.getNames()).contains("/" + name))
  //                .map(Container::getId)
  //                .findFirst()
  //                .orElse(null)
  //    }

  def imageExists(imageName: String): Boolean = DOCKER_CLIENT.listImagesCmd().exec().asScala map (_.getRepoTags) exists (_ contains imageName)

  def pullImage(imageName: String): Boolean = try {
    val callback = new PullImageResultCallback()
    DOCKER_CLIENT.pullImageCmd(imageName).exec(callback).awaitCompletion()
    true
  } catch {
    case e: InterruptedException =>
      Thread.currentThread().interrupt()
      false
  }

  //    String runContainer(ProgLanguage language, Path mountingDir, String resultFile) {
  //        try {
  //            if (!checkImage(IMAGE_PYTHON)) // NOSONAR
  //                return "FEHLER!"
  //
  //            String oldContainerID = getContainerID(CONTAINER_NAME)
  //            if (oldContainerID != null) {
  //                 DOCKER_CLIENT.stopContainerCmd(oldContainerID).exec()
  //                DOCKER_CLIENT.removeContainerCmd(oldContainerID).exec()
  //            }
  //
  //            String containerID = createContainer(language.dockerImageName, mountingDir)
  //
  //            DOCKER_CLIENT.startContainerCmd(containerID).exec()
  //
  //            int statusCode = DOCKER_CLIENT.waitContainerCmd(containerID).exec(new WaitContainerResultCallback())
  //                    .awaitStatusCode(2, TimeUnit.SECONDS)
  //
  //            Logger.debug(String.valueOf(statusCode))
  //
  //            DOCKER_CLIENT.stopContainerCmd(containerID).exec()
  //
  //            return String.join("\n", Files.readAllLines(Paths.get(mountingDir.toString(), resultFile)))
  //        } catch (Exception e) {
  //            Logger.error("There has been an error!", e)
  //            return "TODO!"
  //        }
  //    }

}
