package model.querycorrectors;

import model.CorrectionException;
import model.matching.ScalaMatch;

import model.matching.MatchType;
import play.Logger;
import play.twirl.api.Html;

case class ColumnMatch(userCol: Option[ColumnWrapper], sampleCol: Option[ColumnWrapper]) extends ScalaMatch[ColumnWrapper](userCol, sampleCol) {

  val hasAlias: Boolean = userArg.get.hasAlias || sampleCol.get.hasAlias

  val restMatched: Boolean = false

  val colNamesMatched = matchType == MatchType.SUCCESSFUL_MATCH || matchType == MatchType.UNSUCCESSFUL_MATCH

  val firstColName = userArg.get.getColName;

  val firstRest = userArg.get.getRest;

  val secondColName = sampleArg.get.getColName;

  val secondRest = sampleArg.get.getRest;

  override def analyze(userArg: ColumnWrapper, sampleArg: ColumnWrapper) = userArg.doMatch(sampleArg)

  override def describe =
    <tr class={ getBSClass }>
  		<td><span class={ matchType.toString }></span></td>
  		<td>
				<span class={ if (colNamesMatched) "text-success" else "text-danger" }>{ firstColName }</span>
    		<span class={ if (restMatched) "text-success" else "text-danger" }>{ firstRest }</span>
  		</td>
 		  <td>
   	 		<span class={ if (colNamesMatched) "text-success" else "text-danger" }>{ secondColName }</span>
    		<span class={ if (restMatched) "text-success" else "text-danger" }>{ secondRest }</span>
  		</td>
    	<td>{ explanation }</td>
		</tr>

}
