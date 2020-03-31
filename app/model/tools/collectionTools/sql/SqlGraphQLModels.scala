package model.tools.collectionTools.sql

import model.core.matching.MatchType
import model.points.Points
import model.tools.collectionTools.sql.SqlToolMain.{ColumnComparison, TableComparison}
import model.tools.collectionTools.sql.matcher.{ColumnMatch, TableMatch}
import model.tools.collectionTools.{SampleSolution, ToolGraphQLModelBasics}
import net.sf.jsqlparser.schema.Table
import sangria.macros.derive._
import sangria.schema._

object SqlGraphQLModels extends ToolGraphQLModelBasics[SqlExerciseContent, String, SqlExPart] {

  override val ExContentTypeType: ObjectType[Unit, SqlExerciseContent] = {
    implicit val sqlExerciseTypeType: EnumType[SqlExerciseType] = deriveEnumType()

    implicit val sqlSampleSolutionType: ObjectType[Unit, SampleSolution[String]] = stringSampleSolutionType

    deriveObjectType()
  }

  // Solution types

  override val SolTypeInputType: InputType[String] = StringType

  // Result types

  private val columnMatchType: ObjectType[Unit, ColumnMatch] = {
    implicit val mtt: EnumType[MatchType] = matchTypeType
    implicit val columnWrapperType: ObjectType[Unit, ColumnWrapper] = ObjectType(
      "ColumnWrapper",
      fields[Unit, ColumnWrapper](
        Field("name", StringType, resolve = _.value.getColName)
      )
    )

    deriveObjectType()
  }

  private val tableMatchType: ObjectType[Unit, TableMatch] = {
    implicit val mtt: EnumType[MatchType] = matchTypeType
    implicit val tableType: ObjectType[Unit, Table] = ObjectType(
      "Table",
      fields[Unit, Table](
        Field("name", StringType, resolve = _.value.getName)
      )
    )

    deriveObjectType()
  }

  private val sqlQueriesStaticComparisonType: ObjectType[Unit, SqlQueriesStaticComparison] = {

    implicit val cct: ObjectType[Unit, ColumnComparison] = matchingResultType("SqlColumnComparison", columnMatchType)
    implicit val tct: ObjectType[Unit, TableComparison]  = matchingResultType("SqlTableComparison", tableMatchType)

    deriveObjectType(
      // TODO: do not exclude fields...
      ExcludeFields(
        "joinExpressionComparison",
        "whereComparison",
        "additionalComparisons"
      )
    )
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
