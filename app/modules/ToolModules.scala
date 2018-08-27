package modules

import com.google.inject.AbstractModule
import com.google.inject.multibindings.Multibinder
import com.typesafe.config.Config
import model.toolMains.AToolMain
import play.api.{ConfigLoader, Configuration, Environment}

case class ToolConfig(toolName: String, toolMainClass: String, isEnabled: Boolean = true)

class ToolModules(environment: Environment, configuration: Configuration) extends AbstractModule {

  private implicit val configLoader: ConfigLoader[ToolConfig] = (rootConfig: Config, path: String) => {
    val config = rootConfig.getConfig(path)

    ToolConfig(
      toolName = config.getString("name"),
      toolMainClass = config.getString("toolMain"),
      isEnabled = if (config.hasPath("enabled")) config.getBoolean("enabled") else true
    )
  }

  override def configure(): Unit = {

    val modulesConfig = configuration.get[Configuration]("modules")

    val multiBinder: Multibinder[AToolMain] = Multibinder.newSetBinder(binder(), classOf[AToolMain])

    // TODO: get by annotation...
    modulesConfig.subKeys
      .map(modulesConfig.get[ToolConfig]) // Load configuration for tool
      .filter(_.isEnabled)
      .map(tc => this.getClass.getClassLoader.loadClass(tc.toolMainClass))
      .foreach {
        case classOfMain: Class[AToolMain] => multiBinder.addBinding().to(classOfMain)
        case o                             => println(o)
      }

    DockerPullsStartTask.pullImages()

  }

}