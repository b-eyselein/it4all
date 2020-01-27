package model.tools.collectionTools.sql

import model.tools.collectionTools.sql.matcher._
import model.tools.collectionTools.{ExerciseCollection, SampleSolution}
import net.sf.jsqlparser.expression.{BinaryExpression, Expression}
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.Statement
import play.api.Logger

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

abstract class QueryCorrector(val queryType: String) {

  private val logger = Logger(classOf[QueryCorrector])

  protected type Q <: net.sf.jsqlparser.statement.Statement


  private final case class QueryAndStaticComp[Q](query: Q, comp: SqlQueriesStaticComparison)

  private val queryAndStaticCompOrdering = new Ordering[QueryAndStaticComp[Q]] {

    override def compare(x: QueryAndStaticComp[Q], y: QueryAndStaticComp[Q]): Int =
      y.comp.points.quarters - x.comp.points.quarters

  }


  private def parseSampleAndMakeStaticComparison(
    userQ: Q, sqlSample: SampleSolution[String], userColumns: Seq[ColumnWrapper], userTables: Seq[Table],
    userJoinExpressions: Seq[BinaryExpression], userExpressions: Seq[BinaryExpression], userTableAliases: Map[String, String]
  ): Option[QueryAndStaticComp[Q]] = parseStatement(sqlSample.sample).flatMap(checkStatement) match {
    case Failure(error) =>
      logger.error("There has been an error parsing a sql sample solution", error)
      None

    case Success(sampleQ: Q) =>
      val staticComp = performStaticComparison(userQ, sampleQ, userColumns, userTables, userJoinExpressions, userExpressions, userTableAliases)
      Some(QueryAndStaticComp(sampleQ, staticComp))
  }


  def correct(
    database: SqlExecutionDAO,
    learnerSolution: String,
    exercise: SqlExerciseContent,
    scenario: ExerciseCollection,
    solutionSaved: Boolean
  )(implicit ec: ExecutionContext): Try[SqlResult] = parseStatement(learnerSolution).flatMap(checkStatement).map { userQ =>

    val userColumns         = getColumnWrappers(userQ)
    val userTables          = getTables(userQ)
    val userJoinExpressions = getJoinExpressions(userQ)
    val userExpressions     = getExpressions(userQ)
    val userTableAliases    = resolveAliases(userTables)

    val maybeStaticComparison: Option[QueryAndStaticComp[Q]] =
      exercise.sampleSolutions
        .flatMap { sqlSample => parseSampleAndMakeStaticComparison(userQ, sqlSample, userColumns, userTables, userJoinExpressions, userExpressions, userTableAliases) }
        .minOption(queryAndStaticCompOrdering)

    maybeStaticComparison match {
      case None                                  => ???
      case Some(QueryAndStaticComp(sampleQ, sc)) => SqlResult(sc, database.executeQueries(scenario, exercise, userQ, sampleQ), solutionSaved)
    }

  }


  private def performStaticComparison(
    userQ: Q, sampleQ: Q,
    userColumns: Seq[ColumnWrapper],
    userTables: Seq[Table],
    userJoinExpressions: Seq[BinaryExpression],
    userExpressions: Seq[BinaryExpression],
    userTableAliases: Map[String, String]
  ): SqlQueriesStaticComparison = {

    val sampleTables                        = getTables(sampleQ)
    val sampleTAliases: Map[String, String] = resolveAliases(sampleTables)

    SqlQueriesStaticComparison(
      ColumnMatcher.doMatch(userColumns, getColumnWrappers(sampleQ)),
      TableMatcher.doMatch(userTables, sampleTables),
      new JoinExpressionMatcher(userTableAliases, sampleTAliases).doMatch(userJoinExpressions, getJoinExpressions(sampleQ)),
      new BinaryExpressionMatcher(userTableAliases, sampleTAliases).doMatch(userExpressions, getExpressions(sampleQ)),
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

  protected def performAdditionalComparisons(userQuery: Q, sampleQuery: Q): AdditionalComparison

  // Parsing

  private def parseStatement(str: String): Try[Statement] = Try(CCJSqlParserUtil.parse(str)) match {
    case Failure(e) => Failure(new SqlStatementException(e))
    case other      => other
  }

  protected def checkStatement(statement: Statement): Try[Q] // FIXME: Failure!?!

}
