package modules

import com.google.inject.AbstractModule
import com.typesafe.config.Config
import play.api.{ConfigLoader, Configuration, Environment}

import scala.language.postfixOps

case class ToolConfig(toolName: String, toolMainClass: String, isEnabled: Boolean = true)

class ToolModules(environment: Environment, configuration: Configuration) extends AbstractModule {

  implicit val configLoader: ConfigLoader[ToolConfig] = (rootConfig: Config, path: String) => {
    val config = rootConfig.getConfig(path)

    ToolConfig(
      toolName = config.getString("name"),
      toolMainClass = config.getString("toolMain"),
      isEnabled = if (config.hasPath("enabled")) config.getBoolean("enabled") else true
    )
  }

  override def configure(): Unit = {

    val modulesConfig = configuration.get[Configuration]("modules")

    modulesConfig.subKeys foreach { subKey =>
      // Get configuration for tool
      val toolConfig = modulesConfig.get[ToolConfig](subKey)

      if (toolConfig.isEnabled) {
        // Get class for toolMain
        val classOfMain = this.getClass.getClassLoader.loadClass(toolConfig.toolMainClass)

        // Bind class of toolMain
        bind(classOfMain).asEagerSingleton()
      }
    }


    DockerPullsStartTask.pullImages()

  }

}
