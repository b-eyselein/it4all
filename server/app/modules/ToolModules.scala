package modules

import com.google.inject.AbstractModule
import play.api.{Configuration, Environment}

private final case class ToolConfig(toolName: String, toolMainClass: String, isEnabled: Boolean = true)

class ToolModules() extends AbstractModule {

  override def configure(): Unit = {
    DockerPullsStartTask.pullImages()
  }

}
