package model;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.AttachContainerResultCallback;
import com.github.dockerjava.core.command.PullImageResultCallback;

import play.Logger;

public class DockerConnector {

  private static class ACRCB extends AttachContainerResultCallback {

    private String result = "";

    public String getResult() {
      return result;
    }

    @Override
    public void onNext(Frame item) {
      Arrays.asList(item.getPayload()).forEach(System.out::print);
      byte[] bytes = item.getPayload();
      String toAppend = String.format("%s", new String(bytes).trim());
      System.out.println("Bytes:  " + bytes);
      System.out.println("String: " + toAppend);
      result += toAppend + "\n";
    }

  }

  private static final String IMAGE = "alpine:3.5";
  private static final String NAME = "test";

  private static final String CMD = "ls";

  private static final DockerClient DOCKER_CLIENT = DockerClientBuilder.getInstance().build();

  private static void checkImage(String image) throws InterruptedException {
    if(imageExists(image))
      return;

    pullImage(IMAGE);
  }

  private static String getContainerID(String name) {
    return DOCKER_CLIENT.listContainersCmd().withShowAll(true).exec().stream()
        .filter(container -> Arrays.asList(container.getNames()).contains("/" + name)).map(Container::getId).findFirst()
        .orElse(null);
  }

  private static boolean imageExists(String image) {
    return DOCKER_CLIENT.listImagesCmd().exec().stream().map(Image::getRepoTags).map(Arrays::asList)
        .anyMatch(images -> images.contains(image));
  }

  private static void pullImage(String image) throws InterruptedException {
    Logger.debug("Pulling image " + image);

    DOCKER_CLIENT.pullImageCmd(image).exec(new PullImageResultCallback()).awaitCompletion();

    Logger.debug("Image " + image + " was successfully pulled.");
  }

  public String attachContainer() {
    try(ACRCB callback = new ACRCB()) {
      checkImage(IMAGE);

      String containerID = getContainerID(NAME);
      if(containerID == null)
        containerID = DOCKER_CLIENT.createContainerCmd(IMAGE).withName(NAME).withCmd(CMD).withTty(false).exec().getId();

      DOCKER_CLIENT.startContainerCmd(containerID).exec();

      DOCKER_CLIENT.attachContainerCmd(containerID).withStdErr(true).withStdOut(true).withFollowStream(true)
          .withLogs(true).exec(callback).awaitCompletion(15, TimeUnit.SECONDS);

      Logger.info("Content callback: " + callback.toString() + " >>" + callback.getResult() + "<<");

      DOCKER_CLIENT.logContainerCmd(containerID).exec(callback);
      return callback.getResult();
    } catch (IOException | NotFoundException | InterruptedException e) {
      Logger.error("Error in DockerConnector:", e);
      return "Error in DockerConnector:" + e.getMessage();
    }
  }

}
