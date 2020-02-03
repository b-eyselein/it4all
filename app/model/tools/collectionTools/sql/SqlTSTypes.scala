package model.tools.collectionTools.sql

import model.core.matching.MatchType
import model.tools.collectionTools.sql.SqlToolMain._
import model.tools.collectionTools.sql.matcher._
import model.tools.collectionTools.{SampleSolution, ToolTSInterfaceTypes}
import net.sf.jsqlparser.expression.operators.relational.ExpressionList
import net.sf.jsqlparser.expression.{BinaryExpression, Expression}
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.select.{Limit, OrderByElement}
import nl.codestar.scalatsi.TypescriptType.{TSString, TypescriptNamedType}
import nl.codestar.scalatsi.{TSIType, TSType}

object SqlTSTypes extends ToolTSInterfaceTypes {

  private implicit val matchTypeTsType: TSType[MatchType] = matchTypeTS

  private val sqlExerciseContentTSI: TSIType[SqlExerciseContent] = {
    implicit val seTypeT: TSType[SqlExerciseType]         = enumTsType(SqlExerciseType)
    implicit val seTagT : TSType[SqlExerciseTag]          = enumTsType(SqlExerciseTag)
    implicit val ssst   : TSIType[SampleSolution[String]] = sampleSolutionTSI("Sql", TSType(TSString))

    TSType.fromCaseClass
  }

  private val sqlQueryResultTSI: TSIType[SqlQueryResult] = {
    implicit val sct: TSType[SqlCell] = TSType.fromCaseClass
    implicit val srt: TSType[SqlRow]  = TSType.fromCaseClass

    TSType.fromCaseClass
  }

  private val columnComparisonTSI: TSIType[ColumnComparison] = matchingResultTSI("Column", {
    implicit val ct: TSType[ColumnWrapper] = TSType(TSString)

    TSType.fromCaseClass[ColumnMatch]
  })

  private val tableComparisonTSI: TSIType[TableComparison] = matchingResultTSI("Table", {
    implicit val tt: TSType[Table] = TSType(TSString)

    TSType.fromCaseClass[TableMatch]
  })

  private val binaryExpressionComparisonTSI: TSIType[BinaryExpressionComparison] = TSIType({

    val binExMatchTSI: TSIType[BinaryExpressionMatch] = TSIType({
      implicit val bet: TSType[BinaryExpression] = TSType(TSString)

      TSType.fromCaseClass[BinaryExpressionMatch]
      }.get.copy(extending = Some(baseMatchTSI.get))
    )

    matchingResultTSI[BinaryExpression, BinaryExpressionMatch]("BinaryExpression", binExMatchTSI)
      .get.copy(extending = Some(baseMatchingResultTSI.get))
  })

  private val additionalComparisonsTSI: TSIType[AdditionalComparison] = {
    implicit val groupByComparisonTSI: TSIType[GroupByComparison] = matchingResultTSI("GroupBy", {

      implicit val gbt: TSType[Expression] = TSType(TSString)

      TSType.fromCaseClass[GroupByMatch]
    })

    implicit val orderByComparisonTSI: TSIType[OrderByComparison] = matchingResultTSI("OrderBy", {
      implicit val obt: TSType[OrderByElement] = TSType(TSString)

      TSType.fromCaseClass[OrderByMatch]
    })

    implicit val limitComparisonTSI: TSIType[LimitComparison] = matchingResultTSI("Limit", {
      implicit val lt: TSType[Limit] = TSType(TSString)

      TSType.fromCaseClass[LimitMatch]
    })

    implicit val insertComparisonTSI: TSIType[InsertComparison] = matchingResultTSI("ExpressionList", {
      implicit val elt: TSType[ExpressionList] = TSType(TSString)

      TSType.fromCaseClass[ExpressionListMatch]
    })

    TSType.fromCaseClass
  }

  private val sqlExecutionResultTSI: TSIType[SqlExecutionResult] = {

    implicit val sqlRowTSI: TSIType[SqlRow] = {
      implicit val sqlCellTSI: TSIType[SqlCell] = TSType.fromCaseClass

      TSType.fromCaseClass
    }

    TSType.fromCaseClass
  }

  private val sqlResultTSI: TSIType[SqlResult] = {
    implicit val cct: TSIType[ColumnComparison] = columnComparisonTSI

    implicit val tct: TSIType[TableComparison] = tableComparisonTSI

    implicit val ject: TSIType[BinaryExpressionComparison] = binaryExpressionComparisonTSI

    implicit val act: TSIType[AdditionalComparison] = additionalComparisonsTSI

    implicit val ert: TSIType[SqlExecutionResult] = sqlExecutionResultTSI

    TSType.fromCaseClass
  }

  val exported: Seq[TypescriptNamedType] = Seq(
    sqlExerciseContentTSI.get,
    sqlQueryResultTSI.get,
    sqlResultTSI.get,
  )

}
