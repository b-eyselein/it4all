package model.tools.sql

import model.SampleSolution
import model.result.InternalErrorResult
import model.points._
import model.tools.AbstractCorrector
import model.tools.sql.matcher._
import net.sf.jsqlparser.expression.{BinaryExpression, Expression}
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.Statement

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Try}

abstract class QueryCorrector(val queryType: String) extends AbstractCorrector {

  override type AbstractResult = SqlAbstractResult

  override protected def buildInternalError(
    msg: String,
    solutionSaved: Boolean,
    maxPoints: Points
  ): SqlAbstractResult with InternalErrorResult = SqlInternalErrorResult(msg, solutionSaved, maxPoints)

  protected type Q <: net.sf.jsqlparser.statement.Statement

  private val queryAndStaticCompOrdering: Ordering[(Q, SqlQueriesStaticComparison)] = (x, y) =>
    y._2.points.quarters - x._2.points.quarters

  def correct(
    database: SqlExecutionDAO,
    schemaName: String,
    learnerSolution: String,
    sampleSolutions: Seq[SampleSolution[String]],
    solutionSaved: Boolean
  )(implicit ec: ExecutionContext): SqlAbstractResult =
    parseStatement(learnerSolution).fold(
      exception => SqlInternalErrorResult("Your query could not be parsed: " + exception.getMessage, solutionSaved),
      userQ =>
        checkStatement(userQ).fold(
          _ => SqlInternalErrorResult("Wrong type of statement!", solutionSaved),
          userQ => {
            val userColumns         = getColumnWrappers(userQ)
            val userTables          = getTables(userQ)
            val userJoinExpressions = getJoinExpressions(userQ)
            val userExpressions     = getExpressions(userQ)
            val userTableAliases    = resolveAliases(userTables)

            val maybeStaticComparison: Option[(Q, SqlQueriesStaticComparison)] =
              sampleSolutions
                .flatMap { sqlSample =>
                  parseStatement(sqlSample.sample).toOption
                    .flatMap(ps => checkStatement(ps).toOption)
                    .map { sampleQ =>
                      val staticComp = performStaticComparison(
                        userQ,
                        sampleQ,
                        userColumns,
                        userTables,
                        userJoinExpressions,
                        userExpressions,
                        userTableAliases
                      )
                      (sampleQ, staticComp)
                    }

                }
                .minOption(queryAndStaticCompOrdering)

            maybeStaticComparison match {
              case None => ???
              case Some((sampleQ, sc)) =>
                val executionResult = database.executeQueries(schemaName, userQ, sampleQ)

                SqlResult(sc, executionResult, solutionSaved)
            }
          }
        )
    )

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
      TableMatcher.doMatch(userTables, sampleTables),
      binExMatcher.doMatch(userJoinExpressions, getJoinExpressions(sampleQ)),
      binExMatcher.doMatch(userExpressions, getExpressions(sampleQ)),
      performAdditionalComparisons(userQ, sampleQ)
    )
  }

  private def getExpressions(statement: Q): Seq[BinaryExpression] =
    getWhere(statement) match {
      case None             => Seq[BinaryExpression]()
      case Some(expression) => new ExpressionExtractor(expression).extracted
    }

  private def resolveAliases(tables: Seq[Table]): Map[String, String] =
    tables
      .filter(q => Option(q.getAlias).isDefined)
      .map(t => t.getAlias.getName -> t.getName)
      .toMap

  protected def getColumnWrappers(query: Q): Seq[ColumnWrapper]

  protected def getTables(query: Q): Seq[Table]

  protected def getJoinExpressions(query: Q): Seq[BinaryExpression] = Seq.empty

  protected def getWhere(query: Q): Option[Expression]

  protected def performAdditionalComparisons(userQuery: Q, sampleQuery: Q): AdditionalComparison

  // Parsing

  private def parseStatement(str: String): Try[Statement] =
    Try(CCJSqlParserUtil.parse(str)) match {
      case Failure(e) => Failure(new SqlStatementException(e))
      case other      => other
    }

  protected def checkStatement(statement: Statement): Try[Q] // FIXME: Failure!?!

}
