package model.tools.sql

import model.graphql.{GraphQLArguments, ToolGraphQLModelBasics}
import model.matching.StringMatcher.StringMatchingResult
import model.tools.sql.SqlTool._
import model.tools.sql.matcher._
import net.sf.jsqlparser.expression.BinaryExpression
import net.sf.jsqlparser.statement.select.Limit
import sangria.macros.derive._
import sangria.schema._

object SqlGraphQLModels
    extends ToolGraphQLModelBasics[String, SqlExerciseContent, SqlExPart, SqlResult]
    with GraphQLArguments {

  override val partEnumType: EnumType[SqlExPart] = EnumType(
    "SqlExPart",
    values = SqlExPart.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

  private val sqlExerciseTypeType: EnumType[SqlExerciseType] = deriveEnumType()

  // Solution types

  override val solutionInputType: InputType[String] = StringType

  // Result types

  private val sqlSelectAdditionalComparisons: ObjectType[Unit, SelectAdditionalComparisons] = {
    implicit val smrt: ObjectType[Unit, StringMatchingResult] = stringMatchingResultType

    implicit val lct: ObjectType[Unit, LimitComparison] = matchingResultType(
      "SqlLimitComparison",
      buildStringMatchTypeType[Limit, LimitMatch]("SqlLimitMatch"),
      StringType,
      _.toString
    )

    deriveObjectType()
  }

  private val additionalComparisonsType: ObjectType[Unit, AdditionalComparison] = {
    implicit val ssac: ObjectType[Unit, SelectAdditionalComparisons] = sqlSelectAdditionalComparisons
    implicit val smrt: ObjectType[Unit, StringMatchingResult]        = stringMatchingResultType

    deriveObjectType()
  }

  private val sqlQueriesStaticComparisonType: ObjectType[Unit, SqlQueriesStaticComparison] = {

    implicit val smrt: ObjectType[Unit, StringMatchingResult] = stringMatchingResultType

    implicit val cct: ObjectType[Unit, ColumnComparison] = matchingResultType(
      "SqlColumnComparison",
      buildStringMatchTypeType[ColumnWrapper, ColumnMatch]("SqlColumnMatch"),
      StringType,
      _.toString
    )

    implicit val jct: ObjectType[Unit, BinaryExpressionComparison] = matchingResultType(
      "SqlBinaryExpressionComparison",
      buildStringMatchTypeType[BinaryExpression, BinaryExpressionMatch]("SqlBinaryExpressionMatch"),
      StringType,
      _.toString
    )

    implicit val act: ObjectType[Unit, AdditionalComparison] = additionalComparisonsType

    deriveObjectType()
  }

  private val sqlCellType: ObjectType[Unit, SqlCell] = deriveObjectType()

  private val KeyCellValueObjectType = ObjectType(
    "SqlKeyCellValueObject",
    fields[Unit, (String, SqlCell)](
      Field("key", StringType, resolve = _.value._1),
      Field("value", sqlCellType, resolve = _.value._2)
    )
  )

  private val sqlRowType: ObjectType[Unit, SqlRow] =
    deriveObjectType(
      ReplaceField("cells", Field("cells", ListType(KeyCellValueObjectType), resolve = _.value.cells.toSeq))
    )

  private val sqlQueryResultType: ObjectType[Unit, SqlQueryResult] = {
    implicit val srt: ObjectType[Unit, SqlRow] = sqlRowType

    deriveObjectType()
  }

  private val sqlExecutionResultType: ObjectType[Unit, SqlExecutionResult] = {
    implicit val sqrt: ObjectType[Unit, SqlQueryResult] = sqlQueryResultType

    deriveObjectType()
  }

  // Abstract result

  override val resultType: OutputType[SqlResult] = {
    implicit val sqsct: ObjectType[Unit, SqlQueriesStaticComparison] = sqlQueriesStaticComparisonType
    implicit val sert: ObjectType[Unit, SqlExecutionResult]          = sqlExecutionResultType

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
    implicit val seTypeT: EnumType[SqlExerciseType] = sqlExerciseTypeType

    deriveObjectType(
      AddFields(
        Field(
          "part",
          OptionType(partEnumType),
          arguments = partIdArgument :: Nil,
          resolve = context => SqlExPart.values.find(_.id == context.arg(partIdArgument))
        ),
        dbContentQueryField
      )
    )
  }

}
