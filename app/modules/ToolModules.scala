package modules

import com.google.inject.AbstractModule
import play.api.{Configuration, Environment}

class ToolModules(environment: Environment, configuration: Configuration) extends AbstractModule {

  override def configure(): Unit = {

    // Random Exes

    model.bool.BoolToolMain

    model.nary.NaryToolMain

    // File Exes

    bind(classOf[model.spread.SpreadToolMain]).asEagerSingleton()

    bind(classOf[model.mindmap.MindmapToolMain]).asEagerSingleton()

    // Id Part Exes

    bind(classOf[model.blanks.BlanksToolMain]).asEagerSingleton()

    bind(classOf[model.programming.ProgrammingToolMain]).asEagerSingleton()

    bind(classOf[model.rose.RoseToolMain]).asEagerSingleton()

    bind(classOf[model.uml.UmlToolMain]).asEagerSingleton()

    bind(classOf[model.web.WebToolMain]).asEagerSingleton()

    bind(classOf[model.xml.XmlToolMain]).asEagerSingleton()

    // Ex Collections

    bind(classOf[model.sql.SqlToolMain]).asEagerSingleton()

    bind(classOf[model.questions.QuestionToolMain]).asEagerSingleton()

  }

}
