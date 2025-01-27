package model.tools.sql

import model.matching.StringMatcher
import model.tools.CorrectionException
import model.tools.sql.matcher._
import net.sf.jsqlparser.expression.{BinaryExpression, Expression}
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.Statement
import play.api.Logger

import scala.annotation.unused
import scala.util.{Failure, Success, Try}

final case class ExtractedQuery[Q](
  query: Q,
  columns: Seq[ColumnWrapper],
  tables: Seq[Table],
  tableAliases: Map[String, String],
  joinExpressions: Seq[BinaryExpression],
  whereExpressions: Seq[BinaryExpression]
)

abstract class QueryCorrector(val queryType: String) {

  protected type Q <: Statement

  private val logger = Logger(classOf[QueryCorrector])

  protected val sqlExecutionDAO: SqlExecutionDAO[Q]

  private val queryAndStaticCompOrdering: Ordering[(Q, SqlQueriesStaticComparison)] = (x, y) => y._2.points.quarters - x._2.points.quarters

  private def extractQuery(query: Q): ExtractedQuery[Q] = {
    val userTables = getTables(query)

    ExtractedQuery(
      query,
      getColumnWrappers(query),
      userTables,
      resolveAliases(userTables),
      getJoinExpressions(query),
      getExpressions(query)
    )
  }

  def correct(schemaName: String, learnerSolution: String, sampleSolutions: Seq[String]): Try[SqlResult] = for {
    _ <-
      if (learnerSolution.trim.isEmpty) {
        Failure(new CorrectionException("Learner solution is empty!"))
      } else {
        Success(())
      }

    parsedUserQ <- parseStatement(learnerSolution)
    userQ       <- checkStatement(parsedUserQ)

    userQuery = extractQuery(userQ)

    staticComparisons: Seq[(Q, SqlQueriesStaticComparison)] = sampleSolutions
      .map { sample =>
        for {
          parsedSample <- parseStatement(sample)
          sample       <- checkStatement(parsedSample)

          staticComparison = performStaticComparison(userQuery, extractQuery(sample))
        } yield (sample, staticComparison)
      }
      .flatMap {
        case Success(value) => Some(value)
        case Failure(exception) =>
          logger.error("One of the sample solutions for sql is not valid:", exception)
          None
      }

    result <- staticComparisons.minOption(queryAndStaticCompOrdering) match {
      case None                => Failure(new CorrectionException("Could not compare to a valid sample solution!"))
      case Some((sampleQ, sc)) => Success(SqlResult(sc, sqlExecutionDAO.executeQueries(schemaName, userQ, sampleQ)))
    }
  } yield result

  private def performStaticComparison(userQ: ExtractedQuery[Q], sampleQ: ExtractedQuery[Q]): SqlQueriesStaticComparison = {

    val ExtractedQuery(userQuery, userColumns, userTables, userTableAliases, userJoinExpressions, userWhereExpressions) = userQ

    val ExtractedQuery(sampleQuery, sampleColumns, sampleTables, sampleTableAliases, sampleJoinExpressions, sampleWhereExpressions) = sampleQ

    val binExMatcher = BinaryExpressionMatcher(userTableAliases, sampleTableAliases)

    SqlQueriesStaticComparison(
      ColumnMatcher.doMatch(userColumns, sampleColumns),
      StringMatcher.doMatch(userTables.map(_.getName), sampleTables.map(_.getName)),
      binExMatcher.doMatch(userJoinExpressions, sampleJoinExpressions),
      binExMatcher.doMatch(userWhereExpressions, sampleWhereExpressions),
      performAdditionalComparisons(userQuery, sampleQuery)
    )
  }

  private def getExpressions(statement: Q): Seq[BinaryExpression] = getWhere(statement) match {
    case Some(expression) => new ExpressionExtractor().extractFrom(expression)
    case None             => Seq.empty
  }

  private def resolveAliases(tables: Seq[Table]): Map[String, String] = tables
    .filter { _.getAlias != null }
    .map { t => t.getAlias.getName -> t.getName }
    .toMap

  protected def getColumnWrappers(query: Q): Seq[ColumnWrapper]

  protected def getTables(query: Q): Seq[Table]

  protected def getJoinExpressions(@unused query: Q): Seq[BinaryExpression] = Seq.empty

  protected def getWhere(query: Q): Option[Expression]

  protected def performAdditionalComparisons(@unused userQuery: Q, @unused sampleQuery: Q): AdditionalComparison = AdditionalComparison(None, None)

  // Parsing

  private def parseStatement(str: String): Try[Statement] = Try { CCJSqlParserUtil.parse(str) }

  protected def checkStatement(statement: Statement): Try[Q] // FIXME: Failure!?!

}
