package modules

import com.google.inject.AbstractModule
import model.blanks.BlanksToolMain
import model.mindmap.MindmapToolMain
import model.programming.ProgrammingToolMain
import model.questions.QuestionToolMain
import model.rose.RoseToolMain
import model.spread.SpreadToolMain
import model.sql.SqlToolMain
import model.uml.UmlToolMain
import model.web.WebToolMain
import model.xml.XmlToolMain
import play.api.{Configuration, Environment}

class ToolModules(environment: Environment, configuration: Configuration) extends AbstractModule {

  override def configure(): Unit = {

    // File Exes

    bind(classOf[SpreadToolMain]).asEagerSingleton()

    bind(classOf[MindmapToolMain]).asEagerSingleton()

    // Id Part Exes

    bind(classOf[BlanksToolMain]).asEagerSingleton()

    bind(classOf[ProgrammingToolMain]).asEagerSingleton()

    bind(classOf[RoseToolMain]).asEagerSingleton()

    bind(classOf[UmlToolMain]).asEagerSingleton()

    bind(classOf[WebToolMain]).asEagerSingleton()

    bind(classOf[XmlToolMain]).asEagerSingleton()

    // Ex Collections

    bind(classOf[SqlToolMain]).asEagerSingleton()

    bind(classOf[QuestionToolMain]).asEagerSingleton()

  }

}
