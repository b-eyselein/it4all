package model.sql

import model.core.matching.{Match, MatchingResult}
import model.sql.matcher._
import net.sf.jsqlparser.expression.{BinaryExpression, Expression}
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.Statement
import play.api.Logger

import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

abstract class QueryCorrector(val queryType: String) {

  protected type Q <: net.sf.jsqlparser.statement.Statement

  def correct(database: SqlExecutionDAO, learnerSolution: String, allSampleSolutions: Seq[SqlSample], exercise: SqlCompleteEx, scenario: SqlScenario): SqlCorrResult =
    parseStatement(learnerSolution) flatMap checkStatement match {
      case Failure(error) => SqlParseFailed(learnerSolution, error)
      case Success(userQ) =>

        val userColumns = getColumnWrappers(userQ)
        val userTables = getTables(userQ)
        val userJoinExpressions = getJoinExpressions(userQ)
        val userExpressions = getExpressions(userQ)
        val userTableAliases = resolveAliases(userTables)

        val maybeStaticComparison: Option[SqlQueriesStaticComparison[Q]] = exercise.samples.map { sqlSample =>
          parseStatement(sqlSample.sample) flatMap checkStatement match {
            case Failure(error)      =>
              Logger.error("There has been an error parsing a sql sample solution", error)
              ???
            case Success(sampleQ: Q) => performStaticComparison(userQ, sampleQ, userColumns, userTables, userJoinExpressions, userExpressions, userTableAliases)
          }
        } reduceOption { (comp1, comp2) =>
          // FIXME: minByOption with Scala 2.13...
          if (comp1.points > comp2.points) comp1
          else if (comp1.points == comp2.points) {
            if (comp1.maxPoints > comp2.maxPoints) comp2
            else comp1
          } else comp2
        }

        maybeStaticComparison match {
          case None     => ???
          case Some(sc) => SqlResult(learnerSolution, sc.columnComparison, sc.tableComparison, sc.joinExpressionComparison,
            sc.whereComparison, sc.additionalComparisons, database.executeQueries(scenario, exercise, sc.userQ, sc.sampleQ))
        }
    }

  private def performStaticComparison(userQ: Q, sampleQ: Q, userColumns: Seq[ColumnWrapper], userTables: Seq[Table],
                                      userJoinExpressions: Seq[BinaryExpression], userExpressions: Seq[BinaryExpression],
                                      userTableAliases: Map[String, String]): SqlQueriesStaticComparison[Q] = {

    val sampleColumns = getColumnWrappers(sampleQ)
    val sampleTables = getTables(sampleQ)
    val sampleJoinExpressions = getJoinExpressions(sampleQ)
    val sampleExpressions = getExpressions(sampleQ)
    val sampleTAliases: Map[String, String] = resolveAliases(sampleTables)

    SqlQueriesStaticComparison(userQ, sampleQ,
      ColumnMatcher.doMatch(userColumns, sampleColumns),
      TableMatcher.doMatch(userTables, sampleTables),
      new JoinExpressionMatcher(userTableAliases, sampleTAliases).doMatch(userJoinExpressions, sampleJoinExpressions),
      new BinaryExpressionMatcher(userTableAliases, sampleTAliases).doMatch(userExpressions, sampleExpressions),
      performAdditionalComparisons(userQ, sampleQ)
    )
  }

  private def getExpressions(statement: Q): Seq[BinaryExpression] = getWhere(statement) match {
    case None             => Seq[BinaryExpression]()
    case Some(expression) => new ExpressionExtractor(expression).extracted
  }

  private def resolveAliases(tables: Seq[Table]): Map[String, String] =
    tables
      .filter(q => Option(q.getAlias).isDefined)
      .map(t => t.getAlias.getName -> t.getName).toMap

  protected def getColumnWrappers(query: Q): Seq[ColumnWrapper]

  protected def getTables(query: Q): Seq[Table]

  protected def getJoinExpressions(query: Q): Seq[BinaryExpression] = Seq.empty

  protected def getWhere(query: Q): Option[Expression]

  protected def performAdditionalComparisons(userQuery: Q, sampleQuery: Q): Seq[MatchingResult[_ <: Match]]

  // Parsing

  private def parseStatement(str: String): Try[Statement] = Try(CCJSqlParserUtil.parse(str)) match {
    case Failure(e) => Failure(new SqlStatementException(e))
    case other      => other
  }

  protected def checkStatement(statement: Statement): Try[Q] // FIXME: Failure!?!

}
