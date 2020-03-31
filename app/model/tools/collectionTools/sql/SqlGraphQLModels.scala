package model.tools.collectionTools.sql

import model.points.Points
import model.tools.collectionTools.sql.SqlToolMain._
import model.tools.collectionTools.sql.matcher._
import model.tools.collectionTools.{SampleSolution, ToolGraphQLModelBasics}
import sangria.macros.derive._
import sangria.schema._

object SqlGraphQLModels extends ToolGraphQLModelBasics[SqlExerciseContent, String, SqlExPart] {

  private implicit val mtt = matchTypeType

  override val ExContentTypeType: ObjectType[Unit, SqlExerciseContent] = {
    implicit val sqlExerciseTypeType: EnumType[SqlExerciseType] = deriveEnumType()

    implicit val sqlSampleSolutionType: ObjectType[Unit, SampleSolution[String]] = stringSampleSolutionType

    deriveObjectType()
  }

  // Solution types

  override val SolTypeInputType: InputType[String] = StringType

  // Result types

  private val columnMatchType: ObjectType[Unit, ColumnMatch] = deriveObjectType(
    ReplaceField("userArg", Field("userArg", OptionType(StringType), resolve = _.value.userArg.map(_.getColName))),
    ReplaceField("sampleArg", Field("sampleArg", OptionType(StringType), resolve = _.value.userArg.map(_.getColName)))
  )

  private val tableMatchType: ObjectType[Unit, TableMatch] = deriveObjectType(
    ReplaceField("userArg", Field("userArg", OptionType(StringType), resolve = _.value.userArg.map(_.getName))),
    ReplaceField("sampleArg", Field("sampleArg", OptionType(StringType), resolve = _.value.userArg.map(_.getName)))
  )

  private val binaryExpressionMatchType: ObjectType[Unit, BinaryExpressionMatch] = deriveObjectType(
    ReplaceField("userArg", Field("userArg", OptionType(StringType), resolve = _.value.userArg.map(_.toString))),
    ReplaceField("sampleArg", Field("sampleArg", OptionType(StringType), resolve = _.value.userArg.map(_.toString)))
  )

  private val groupByMatchType: ObjectType[Unit, GroupByMatch] = deriveObjectType(
    ReplaceField("userArg", Field("userArg", OptionType(StringType), resolve = _.value.userArg.map(_.toString))),
    ReplaceField("sampleArg", Field("sampleArg", OptionType(StringType), resolve = _.value.userArg.map(_.toString)))
  )

  private val orderByMatchType: ObjectType[Unit, OrderByMatch] = deriveObjectType(
    ReplaceField("userArg", Field("userArg", OptionType(StringType), resolve = _.value.userArg.map(_.toString))),
    ReplaceField("sampleArg", Field("sampleArg", OptionType(StringType), resolve = _.value.userArg.map(_.toString)))
  )

  private val limitMatchType: ObjectType[Unit, LimitMatch] = deriveObjectType(
    ReplaceField("userArg", Field("userArg", OptionType(StringType), resolve = _.value.userArg.map(_.toString))),
    ReplaceField("sampleArg", Field("sampleArg", OptionType(StringType), resolve = _.value.userArg.map(_.toString)))
  )

  private val insertMatchType: ObjectType[Unit, ExpressionListMatch] = deriveObjectType(
    ReplaceField("userArg", Field("userArg", OptionType(StringType), resolve = _.value.userArg.map(_.toString))),
    ReplaceField("sampleArg", Field("sampleArg", OptionType(StringType), resolve = _.value.userArg.map(_.toString)))
  )

  private val sqlSelectAdditionalComparisons: ObjectType[Unit, SelectAdditionalComparisons] = {
    implicit val gbmt: ObjectType[Unit, GroupByComparison] =
      matchingResultType("SqlGroupByComparison", groupByMatchType)
    implicit val obct: ObjectType[Unit, OrderByComparison] =
      matchingResultType("SqlOrderByComparison", orderByMatchType)
    implicit val lct: ObjectType[Unit, LimitComparison] = matchingResultType("SqlLimitComparison", limitMatchType)

    deriveObjectType()
  }

  private val additionalComparisonsType: ObjectType[Unit, AdditionalComparison] = {
    implicit val ssac: ObjectType[Unit, SelectAdditionalComparisons] = sqlSelectAdditionalComparisons
    implicit val sqct: ObjectType[Unit, InsertComparison]            = matchingResultType("SqlInsertComparison", insertMatchType)

    deriveObjectType()
  }

  private val sqlQueriesStaticComparisonType: ObjectType[Unit, SqlQueriesStaticComparison] = {

    implicit val cct: ObjectType[Unit, ColumnComparison] = matchingResultType("SqlColumnComparison", columnMatchType)
    implicit val tct: ObjectType[Unit, TableComparison]  = matchingResultType("SqlTableComparison", tableMatchType)
    implicit val jct: ObjectType[Unit, BinaryExpressionComparison] =
      matchingResultType("SqlBinaryExpressionComparison", binaryExpressionMatchType)
    implicit val act: ObjectType[Unit, AdditionalComparison] = additionalComparisonsType

    deriveObjectType()
  }

  private val sqlExecutionResultType: ObjectType[Unit, SqlExecutionResult] = {
    deriveObjectType(
      // TODO: do not exclude fields...
      AddFields(Field("X", OptionType(IntType), resolve = _ => None)),
      ExcludeFields("userResultTry", "sampleResultTry")
    )
  }

  private val sqlIllegalQueryResultType: ObjectType[Unit, SqlIllegalQueryResult] = {
    implicit val pt: ObjectType[Unit, Points] = pointsType

    deriveObjectType(Interfaces(abstractResultTypeType))
  }

  private val sqlWrongQueryTypeResult: ObjectType[Unit, SqlWrongQueryTypeResult] = {
    implicit val pt: ObjectType[Unit, Points] = pointsType

    deriveObjectType(Interfaces(abstractResultTypeType))
  }

  private val sqlResultType: ObjectType[Unit, SqlResult] = {
    implicit val sqsct: ObjectType[Unit, SqlQueriesStaticComparison] = sqlQueriesStaticComparisonType
    implicit val sert: ObjectType[Unit, SqlExecutionResult]          = sqlExecutionResultType

    deriveObjectType(Interfaces(abstractResultTypeType))
  }

  override val AbstractResultTypeType: OutputType[Any] = UnionType(
    "SqlAbstractResult",
    types = sqlIllegalQueryResultType :: sqlWrongQueryTypeResult :: sqlResultType :: Nil
  )

  // Part type

  override val PartTypeInputType: EnumType[SqlExPart] = EnumType(
    "SqlExPart",
    values = SqlExParts.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

}
