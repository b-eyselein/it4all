package model.core

import javax.inject._

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

class Repository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile]

    with model.TableDefs

    with model.blanks.BlanksTableDefs
    with model.mindmap.MindmapTableDefs
    with model.programming.ProgTableDefs
    with model.questions.QuestionsTableDefs
    with model.spread.SpreadTableDefs
    with model.sql.SqlTableDefs
    with model.uml.UmlTableDefs
    with model.web.WebTableDefs
    with model.xml.XmlTableDefs
