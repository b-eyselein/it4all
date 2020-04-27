package model.tools

import model.tools.programming.ProgrammingTool
import model.tools.regex.RegexTool
import model.tools.sql.SqlTool
import model.tools.uml.UmlTool
import model.tools.web.WebTool
import model.tools.xml.XmlTool

object ToolList {

  val tools: List[CollectionTool] = List(
    ProgrammingTool,
    RegexTool,
    //    RoseToolMain,
    SqlTool,
    UmlTool,
    WebTool,
    XmlTool
  )

}
