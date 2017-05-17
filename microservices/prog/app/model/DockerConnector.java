package model;

import java.util.Arrays;
import java.util.List;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.LogContainerResultCallback;

public class DockerConnector {
  
  private static final String IMAGE_TO_PULL = "python:latest";
  private static final String CONTAINER_NAME = "python_test";
  
  private static final List<String> COMMAND = Arrays.asList("bash");// , "ls");
                                                                    // //
                                                                    // {"python",
  // "script.py"};
  
  private static final String SCRIPTS_FOLDER = "scripts";
  private static final String WORKDIR_IN_CONTAINER = "/usr/src/myapp";
  
  private DockerConnector() {
  }
  
  public static void main(String[] args) {
    run(IMAGE_TO_PULL);
  }
  
  public static void run(String image) {
    // @formatter:off
    DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
    DockerClient docker = DockerClientBuilder.getInstance(config).build();

//    Volume volume1 = new Volume("/opt/webapp1");
//    Volume volume2 = new Volume("/opt/webapp2");

    CreateContainerResponse container = docker.createContainerCmd(image)
//        .withVolumes(volume1, volume2)
//        .withBinds(new Bind("/src/webapp1", volume1, true), new Bind("/src/webapp2", volume2))
        .withName(CONTAINER_NAME)
        .withAttachStdout(true)
        .withAttachStderr(true)
        .withCmd("true")
        .withCmd(COMMAND)
        .exec();

    docker.startContainerCmd(container.getId()).exec();

    LogContainerResultCallback loggingCallback = new LogContainerResultCallback();

    // this essentially test the since=0 case
    docker.logContainerCmd(container.getId())
        .withStdErr(true)
        .withStdOut(true)
        .withFollowStream(true)
        .withTailAll()
        .exec(loggingCallback);

    System.out.println(loggingCallback.toString());

    // @formatter:on
    
  }
  
}
