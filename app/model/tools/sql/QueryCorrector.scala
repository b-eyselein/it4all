package model.tools.sql

import model.matching.StringMatcher
import model.tools.sql.matcher._
import net.sf.jsqlparser.expression.{BinaryExpression, Expression}
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.Statement

import scala.util.{Failure, Success, Try}

abstract class QueryCorrector(val queryType: String) {

  protected type Q <: net.sf.jsqlparser.statement.Statement

  private val queryAndStaticCompOrdering: Ordering[(Q, SqlQueriesStaticComparison)] = (x, y) => y._2.points.quarters - x._2.points.quarters

  def correct(database: SqlExecutionDAO, schemaName: String, learnerSolution: String, sampleSolutions: Seq[String]): Try[SqlResult] = for {
    parsedUserQ: Statement <- parseStatement(learnerSolution)
    userQ: Q               <- checkStatement(parsedUserQ)

    userTables = getTables(userQ)

    maybeStaticComparison = sampleSolutions
      .flatMap { sample =>
        val sampleQ: Try[Q] = for {
          parsedSample <- parseStatement(sample)
          sample       <- checkStatement(parsedSample)
        } yield sample

        sampleQ.map { sampleQ =>
          val staticComp: SqlQueriesStaticComparison = performStaticComparison(
            userQ,
            sampleQ,
            userColumns = getColumnWrappers(userQ),
            userTables,
            userJoinExpressions = getJoinExpressions(userQ),
            userExpressions = getExpressions(userQ),
            userTableAliases = resolveAliases(userTables)
          )

          (sampleQ, staticComp)
        }.toOption
      }
      .minOption(queryAndStaticCompOrdering)

    result <- maybeStaticComparison match {
      case None                => Failure(new Exception("There has been an internal error!"))
      case Some((sampleQ, sc)) => Success(SqlResult(sc, database.executeQueries(schemaName, userQ, sampleQ)))
    }
  } yield result

  private def performStaticComparison(
    userQ: Q,
    sampleQ: Q,
    userColumns: Seq[ColumnWrapper],
    userTables: Seq[Table],
    userJoinExpressions: Seq[BinaryExpression],
    userExpressions: Seq[BinaryExpression],
    userTableAliases: Map[String, String]
  ): SqlQueriesStaticComparison = {

    val sampleTables                        = getTables(sampleQ)
    val sampleTAliases: Map[String, String] = resolveAliases(sampleTables)

    val binExMatcher = BinaryExpressionMatcher(userTableAliases, sampleTAliases)

    SqlQueriesStaticComparison(
      ColumnMatcher.doMatch(userColumns, getColumnWrappers(sampleQ)),
      StringMatcher.doMatch(userTables.map(_.getName), sampleTables.map(_.getName)),
      binExMatcher.doMatch(userJoinExpressions, getJoinExpressions(sampleQ)),
      binExMatcher.doMatch(userExpressions, getExpressions(sampleQ)),
      performAdditionalComparisons(userQ, sampleQ)
    )
  }

  private def getExpressions(statement: Q): Seq[BinaryExpression] = getWhere(statement) match {
    case None             => Seq[BinaryExpression]()
    case Some(expression) => new ExpressionExtractor(expression).extracted
  }

  private def resolveAliases(tables: Seq[Table]): Map[String, String] = tables
    .filter(q => Option(q.getAlias).isDefined)
    .map(t => t.getAlias.getName -> t.getName)
    .toMap

  protected def getColumnWrappers(query: Q): Seq[ColumnWrapper]

  protected def getTables(query: Q): Seq[Table]

  protected def getJoinExpressions(query: Q): Seq[BinaryExpression] = Seq.empty

  protected def getWhere(query: Q): Option[Expression]

  protected def performAdditionalComparisons(userQuery: Q, sampleQuery: Q): AdditionalComparison = AdditionalComparison(None, None)

  // Parsing

  private def parseStatement(str: String): Try[Statement] = Try(CCJSqlParserUtil.parse(str))
    .recoverWith { e => Failure(new SqlStatementException(e)) }

  protected def checkStatement(statement: Statement): Try[Q] // FIXME: Failure!?!

}
