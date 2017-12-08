package model.core

import javax.inject._

import model.TableDefs
import model.blanks.BlanksTableDefs
import model.ebnf.EbnfExercises
import model.mindmap.MindmapTableDefs
import model.programming.ProgTableDefs
import model.questions.QuestionsTableDefs
import model.spread.SpreadTableDefs
import model.sql.SqlTableDefs
import model.uml.UmlTableDefs
import model.web.WebTableDefs
import model.xml.XmlTableDefs
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

class Repository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile]

    with TableDefs

    with BlanksTableDefs
    with EbnfExercises
    with MindmapTableDefs
    with ProgTableDefs
    with QuestionsTableDefs
    with SpreadTableDefs
    with SqlTableDefs
    with UmlTableDefs
    with WebTableDefs
    with XmlTableDefs
