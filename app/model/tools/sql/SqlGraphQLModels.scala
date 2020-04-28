package model.tools.sql

import model.graphql.{GraphQLArguments, GraphQLContext}
import model.tools.sql.SqlTool._
import model.tools.sql.matcher._
import model.tools.{SampleSolution, ToolGraphQLModelBasics}
import net.sf.jsqlparser.expression.operators.relational.ExpressionList
import net.sf.jsqlparser.expression.{BinaryExpression, Expression}
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.select.{Limit, OrderByElement}
import sangria.macros.derive._
import sangria.schema._

object SqlGraphQLModels
    extends ToolGraphQLModelBasics[String, SqlExerciseContent, SqlExPart, SqlAbstractResult]
    with GraphQLArguments {

  override val sampleSolutionType: ObjectType[Unit, SampleSolution[String]] = buildSampleSolutionType("Sql", StringType)

  override val partEnumType: EnumType[SqlExPart] = EnumType(
    "SqlExPart",
    values = SqlExPart.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

  private val sqlExerciseTypeType: EnumType[SqlExerciseType] = deriveEnumType()

  // Solution types

  override val SolTypeInputType: InputType[String] = StringType

  // Result types

  private val columnMatchType: ObjectType[Unit, ColumnMatch] =
    buildStringMatchTypeType[ColumnWrapper, ColumnMatch]("SqlColumnMatch")

  private val tableMatchType: ObjectType[Unit, TableMatch] =
    buildStringMatchTypeType[Table, TableMatch]("SqlTableMatch")

  private val binaryExpressionMatchType: ObjectType[Unit, BinaryExpressionMatch] =
    buildStringMatchTypeType[BinaryExpression, BinaryExpressionMatch]("SqlBinaryExpressionMatch")

  private val groupByMatchType: ObjectType[Unit, GroupByMatch] =
    buildStringMatchTypeType[Expression, GroupByMatch]("SqlGroupByMatch")

  private val orderByMatchType: ObjectType[Unit, OrderByMatch] =
    buildStringMatchTypeType[OrderByElement, OrderByMatch]("SqlOrderByMatch")

  private val limitMatchType: ObjectType[Unit, LimitMatch] =
    buildStringMatchTypeType[Limit, LimitMatch]("SqlLimitMatch")

  private val insertMatchType: ObjectType[Unit, ExpressionListMatch] =
    buildStringMatchTypeType[ExpressionList, ExpressionListMatch]("SqlInsertMatch")

  private val sqlSelectAdditionalComparisons: ObjectType[Unit, SelectAdditionalComparisons] = {
    implicit val gbmt: ObjectType[Unit, GroupByComparison] =
      matchingResultType("SqlGroupByComparison", groupByMatchType)
    implicit val obct: ObjectType[Unit, OrderByComparison] =
      matchingResultType("SqlOrderByComparison", orderByMatchType)
    implicit val lct: ObjectType[Unit, LimitComparison] =
      matchingResultType("SqlLimitComparison", limitMatchType)

    deriveObjectType()
  }

  private val additionalComparisonsType: ObjectType[Unit, AdditionalComparison] = {
    implicit val ssac: ObjectType[Unit, SelectAdditionalComparisons] = sqlSelectAdditionalComparisons
    implicit val sqct: ObjectType[Unit, InsertComparison] =
      matchingResultType("SqlInsertComparison", insertMatchType)

    deriveObjectType()
  }

  private val sqlQueriesStaticComparisonType: ObjectType[Unit, SqlQueriesStaticComparison] = {
    implicit val cct: ObjectType[Unit, ColumnComparison] =
      matchingResultType("SqlColumnComparison", columnMatchType)
    implicit val tct: ObjectType[Unit, TableComparison] =
      matchingResultType("SqlTableComparison", tableMatchType)
    implicit val jct: ObjectType[Unit, BinaryExpressionComparison] =
      matchingResultType("SqlBinaryExpressionComparison", binaryExpressionMatchType)

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

  private val sqlAbstractResultType: InterfaceType[Unit, SqlAbstractResult] = InterfaceType(
    "SqlAbstractResult",
    fields[Unit, SqlAbstractResult](
      Field("solutionSaved", BooleanType, resolve = _.value.solutionSaved),
      Field("points", FloatType, resolve = _.value.points.asDouble),
      Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble)
    ),
    interfaces[Unit, SqlAbstractResult](abstractResultInterfaceType)
  ).withPossibleTypes(() =>
    List(sqlInternalErrorResultType, sqlIllegalQueryResultType, sqlWrongQueryTypeResult, sqlResultType)
  )

  private val sqlInternalErrorResultType: ObjectType[Unit, SqlInternalErrorResult] = deriveObjectType(
    Interfaces(sqlAbstractResultType),
    ExcludeFields("maxPoints")
  )

  private val sqlIllegalQueryResultType: ObjectType[Unit, SqlIllegalQueryResult] = deriveObjectType(
    Interfaces(sqlAbstractResultType),
    ExcludeFields("solutionSaved", "maxPoints")
  )

  private val sqlWrongQueryTypeResult: ObjectType[Unit, SqlWrongQueryTypeResult] = deriveObjectType(
    Interfaces(sqlAbstractResultType),
    ExcludeFields("solutionSaved", "maxPoints")
  )

  private val sqlResultType: ObjectType[Unit, SqlResult] = {
    implicit val sqsct: ObjectType[Unit, SqlQueriesStaticComparison] = sqlQueriesStaticComparisonType
    implicit val sert: ObjectType[Unit, SqlExecutionResult]          = sqlExecutionResultType

    deriveObjectType(Interfaces(sqlAbstractResultType))
  }

  override val toolAbstractResultTypeInterfaceType: InterfaceType[Unit, SqlAbstractResult] = sqlAbstractResultType

  private val dbContentQueryField: Field[GraphQLContext, SqlExerciseContent] = Field(
    "sqlDbContents",
    ListType(SqlGraphQLModels.sqlQueryResultType),
    resolve = context => SelectDAO.tableContents(context.value.schemaName)
  )

  private val sqlExerciseContentType: ObjectType[GraphQLContext, SqlExerciseContent] = {
    implicit val seTypeT: EnumType[SqlExerciseType]            = sqlExerciseTypeType
    implicit val sst: ObjectType[Unit, SampleSolution[String]] = sampleSolutionType

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

  override val exerciseContentType: OutputType[SqlExerciseContent] = sqlExerciseContentType

}
