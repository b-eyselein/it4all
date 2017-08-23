package model.conditioncorrector;

import net.sf.jsqlparser.expression.BinaryExpression
import net.sf.jsqlparser.expression.Expression

class ExtractedExpressions(binaryExpressions: List[BinaryExpression], otherExpressions: List[Expression]) {

  def isEmpty() = binaryExpressions.isEmpty && otherExpressions.isEmpty

}
