package model.sql

import model.Enums.MatchType
import model.core.matching.{Match, Matcher, MatchingResult}
import model.sql.BinaryExpressionMatcher._
import model.sql.SqlConsts.CONDITIONS_NAME
import net.sf.jsqlparser.expression.BinaryExpression
import net.sf.jsqlparser.schema.Column
import play.twirl.api.Html

object BinaryExpressionMatcher {

  val BinaryExpressionHeadings = Seq("TODO!")

  def compareExpressions(binEx1: BinaryExpression, userTAliases: Map[String, String],
                         binEx2: BinaryExpression, sampleTAliases: Map[String, String]): Boolean = {

    def colNameAndAlias(col: Column) = (col.getColumnName, col.getTable.getName)

    val (column1, alias1) = colNameAndAlias(getColToCompare(binEx1))
    val (column2, alias2) = colNameAndAlias(getColToCompare(binEx2))

    val table1 = if (alias1 == null) "" else userTAliases.getOrElse(alias1, alias1)
    val table2 = if (alias2 == null) "" else sampleTAliases.getOrElse(alias2, alias2)

    column1 == column2 && table1 == table2
  }

  def getColToCompare(expression: BinaryExpression): Column = (expression.getLeftExpression, expression.getRightExpression) match {
    case (left: Column, right: Column) => if (left.toString < right.toString) left else right
    case (left, right: Column)         => right
    case (left: Column, right)         => left
    case (_, _)                        => null // throw new CorrectionException("", "")
  }

}

class BinaryExpressionMatcher(userTAliases: Map[String, String], sampleTAliases: Map[String, String])
  extends Matcher[BinaryExpression, BinaryExpressionMatch, BinaryExpressionMatchingResult](
    BinaryExpressionHeadings, compareExpressions(_, userTAliases, _, sampleTAliases), BinaryExpressionMatch, BinaryExpressionMatchingResult)

case class BinaryExpressionMatch(userArg: Option[BinaryExpression], sampleArg: Option[BinaryExpression]) extends Match[BinaryExpression] {

  override val size: Int = 1

  //  can only be successful match
  override def analyze(a1: BinaryExpression, a2: BinaryExpression): MatchType = MatchType.SUCCESSFUL_MATCH

}

case class BinaryExpressionMatchingResult(allMatches: Seq[BinaryExpressionMatch]) extends MatchingResult[BinaryExpression, BinaryExpressionMatch] {

  override val matchName: String = CONDITIONS_NAME

  override val headings: Seq[String] = BinaryExpressionHeadings

}