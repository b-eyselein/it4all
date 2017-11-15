package model.sql

import model.Enums.MatchType
import model.core.matching.Match

case class ColumnMatch(uc: Option[ColumnWrapper], sc: Option[ColumnWrapper], s: Int)
  extends Match[ColumnWrapper](uc, sc, s) {

  val hasAlias: Boolean = userArg.get.hasAlias || sampleArg.get.hasAlias

  val restMatched: Boolean = false

  val colNamesMatched: Boolean = matchType == MatchType.SUCCESSFUL_MATCH || matchType == MatchType.UNSUCCESSFUL_MATCH

  val firstColName: String = userArg.get.getColName

  val firstRest: String = userArg.get.getRest

  val secondColName: String = sampleArg.get.getColName

  val secondRest: String = sampleArg.get.getRest

  override def analyze(userArg: ColumnWrapper, sampleArg: ColumnWrapper): MatchType = userArg.doMatch(sampleArg)

  //  override def describe =
  //    <tr class={ getBSClass }>
  //  		<td><span class={ matchType.toString }></span></td>
  //  		<td>
  //				<span class={ if (colNamesMatched) "text-success" else "text-danger" }>{ firstColName }</span>
  //    		<span class={ if (restMatched) "text-success" else "text-danger" }>{ firstRest }</span>
  //  		</td>
  // 		  <td>
  //   	 		<span class={ if (colNamesMatched) "text-success" else "text-danger" }>{ secondColName }</span>
  //    		<span class={ if (restMatched) "text-success" else "text-danger" }>{ secondRest }</span>
  //  		</td>
  //    	<td>{ explanation }</td>
  //		</tr>

}
