package model

import model.tools.collectionTools.programming.ProgrammingTSTypes
import model.tools.collectionTools.regex.RegexTSTypes
import model.tools.collectionTools.sql.SqlTSTypes
import model.tools.collectionTools.uml.UmlTSTypes
import model.tools.collectionTools.web.WebTSTypes
import model.tools.collectionTools.xml.XmlTSTypes
import nl.codestar.scalatsi.DefaultTSTypes

object MyTSInterfaceTypes
  extends DefaultTSTypes
    with ProgrammingTSTypes
    with RegexTSTypes
    with SqlTSTypes
    with UmlTSTypes
    with WebTSTypes
    with XmlTSTypes
