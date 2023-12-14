package model.tools.sql

import model.graphql.ToolGraphQLModel
import model.matching.StringMatcher.StringMatchingResult
import model.tools.sql.SqlTool._
import model.tools.sql.matcher._
import net.sf.jsqlparser.expression.BinaryExpression
import sangria.macros.derive._
import sangria.schema._

import scala.annotation.unused

object SqlGraphQLModels extends ToolGraphQLModel[String, SqlExerciseContent, SqlResult] {

  private val sqlExerciseTypeType: EnumType[SqlExerciseType] = deriveEnumType()

  // Solution types

  override val solutionInputType: InputType[String] = StringType

  // Result types

  private val sqlSelectAdditionalComparisons: ObjectType[Unit, SelectAdditionalComparisons] = {
    @unused implicit val smrt: ObjectType[Unit, StringMatchingResult] = stringMatchingResultType

    deriveObjectType()
  }

  private val additionalComparisonsType: ObjectType[Unit, AdditionalComparison] = {
    @unused implicit val ssac: ObjectType[Unit, SelectAdditionalComparisons] = sqlSelectAdditionalComparisons
    @unused implicit val smrt: ObjectType[Unit, StringMatchingResult]        = stringMatchingResultType

    deriveObjectType()
  }

  private val sqlQueriesStaticComparisonType: ObjectType[Unit, SqlQueriesStaticComparison] = {

    @unused implicit val smrt: ObjectType[Unit, StringMatchingResult] = stringMatchingResultType

    @unused implicit val cct: ObjectType[Unit, ColumnComparison] = matchingResultType(
      "SqlColumnComparison",
      buildStringMatchTypeType[ColumnWrapper, ColumnMatch]("SqlColumnMatch"),
      StringType,
      col => col.columnName + col.alias.map("as " + _)
    )

    @unused implicit val jct: ObjectType[Unit, BinaryExpressionComparison] = matchingResultType(
      "SqlBinaryExpressionComparison",
      buildStringMatchTypeType[BinaryExpression, BinaryExpressionMatch]("SqlBinaryExpressionMatch"),
      StringType,
      _.toString
    )

    @unused implicit val act: ObjectType[Unit, AdditionalComparison] = additionalComparisonsType

    deriveObjectType()
  }

  private val sqlCellType: ObjectType[Unit, SqlCell] = deriveObjectType()

  private val KeyCellValueObjectType: ObjectType[Unit, (String, SqlCell)] = ObjectType(
    "SqlKeyCellValueObject",
    fields[Unit, (String, SqlCell)](
      Field("key", StringType, resolve = _.value._1),
      Field("value", sqlCellType, resolve = _.value._2)
    )
  )

  private val sqlRowType: ObjectType[Unit, SqlRow] = deriveObjectType(
    ReplaceField("cells", Field("cells", ListType(KeyCellValueObjectType), resolve = _.value.cells.toSeq))
  )

  private val sqlQueryResultType: ObjectType[Unit, SqlQueryResult] = {
    @unused implicit val srt: ObjectType[Unit, SqlRow] = sqlRowType

    deriveObjectType()
  }

  private val sqlExecutionResultType: ObjectType[Unit, SqlExecutionResult] = {
    @unused implicit val sqrt: ObjectType[Unit, SqlQueryResult] = sqlQueryResultType

    deriveObjectType()
  }

  // Abstract result

  override val resultType: OutputType[SqlResult] = {
    @unused implicit val sqsct: ObjectType[Unit, SqlQueriesStaticComparison] = sqlQueriesStaticComparisonType
    @unused implicit val sert: ObjectType[Unit, SqlExecutionResult]          = sqlExecutionResultType

    deriveObjectType[Unit, SqlResult](
      AddFields(
        Field("points", FloatType, resolve = _.value.points.asDouble),
        Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble)
      )
    )
  }

  private val dbContentQueryField: Field[Unit, SqlExerciseContent] = Field(
    "sqlDbContents",
    ListType(SqlGraphQLModels.sqlQueryResultType),
    resolve = context => SelectDAO.tableContents(context.value.schemaName)
  )

  override val exerciseContentType: ObjectType[Unit, SqlExerciseContent] = {
    @unused implicit val seTypeT: EnumType[SqlExerciseType] = sqlExerciseTypeType

    deriveObjectType(
      AddFields(dbContentQueryField)
    )
  }

}
