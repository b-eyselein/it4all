package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.DockerClientException;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.api.model.VolumesFrom;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.PullImageResultCallback;
import com.github.dockerjava.core.command.WaitContainerResultCallback;

import play.Logger;

public class DockerConnector {

  private static final String COMMAND = "bash"; // "./script.sh";

  private static final DockerClient DOCKER_CLIENT = DockerClientBuilder.getInstance().build();

  private static final String IMAGE_PYTHON = "python:3";
  private static final String CONTAINER_NAME = "python_runner";

  private static boolean checkImage(String image) {
    return imageExists(image) || pullImage(IMAGE_PYTHON);
  }

  private static String createContainer(String image, Path workingDir) {
    Path scriptFile = Paths.get("conf", "resources", "prog", "python.sh").toAbsolutePath();
    System.out.println(scriptFile.toString() + " :: " + scriptFile.toFile().exists());
    Bind scriptBind = new Bind(scriptFile.toString(), new Volume("/data/solutions/developer/prog/1/script.sh"));

    // @formatter:off
    return DOCKER_CLIENT
        .createContainerCmd(image)
        .withName(CONTAINER_NAME)
        .withVolumesFrom(new VolumesFrom("it4all_it4all_1"))
        .withBinds(scriptBind)
        .withWorkingDir(workingDir.toString())
        .withCmd(COMMAND)
        .withTty(true)
        .withStdinOpen(true)
        .exec()
        .getId();
    // @formatter:onf
  }

  private static String getContainerID(String name) {
    // @formatter:off
    return DOCKER_CLIENT
        .listContainersCmd().withShowAll(true) // ps -a
        .exec()
        .stream()
        .filter(container -> Arrays.asList(container.getNames()).contains("/" + name))
        .map(Container::getId)
        .findFirst()
        .orElse(null);
    // @formatter:on
  }

  private static boolean imageExists(String image) {
    // @formatter:off
    return DOCKER_CLIENT
        .listImagesCmd()
        .exec()
        .stream()
        .map(Image::getRepoTags).map(Arrays::asList)
        .anyMatch(images -> images.contains(image));
    // @formatter:on
  }

  private static boolean pullImage(String image) {
    try {
      DOCKER_CLIENT.pullImageCmd(image).exec(new PullImageResultCallback()).awaitCompletion();
      return true;
    } catch (InterruptedException e) {
      Logger.error("There has been an error pulling the image " + image, e);
      Thread.currentThread().interrupt();
      return false;
    }
  }

  public String runContainer(AvailableLanguages language, Path mountingDir, String resultFile) {
    if(!checkImage(IMAGE_PYTHON)) // NOSONAR
      return "FEHLER!";

    String oldContainerID = getContainerID(CONTAINER_NAME);
    if(oldContainerID != null) {
      // DOCKER_CLIENT.stopContainerCmd(oldContainerID).exec();
      DOCKER_CLIENT.removeContainerCmd(oldContainerID).exec();
    }

    String containerID = createContainer(language.getImageName(), mountingDir);

    DOCKER_CLIENT.startContainerCmd(containerID).exec();

    try {
      int statusCode = DOCKER_CLIENT.waitContainerCmd(containerID).exec(new WaitContainerResultCallback())
          .awaitStatusCode(2, TimeUnit.SECONDS);

      Logger.debug(String.valueOf(statusCode));
    } catch (DockerClientException e) {

    }

    DOCKER_CLIENT.stopContainerCmd(containerID).exec();

    try {
      return String.join("\n", Files.readAllLines(Paths.get(mountingDir.toString(), resultFile)));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return "TODO!";
    }
  }

}
