package model.tools

import model.core.matching.{Match, MatchType, MatchingResult}
import model.core.result.{AbstractCorrectionResult, SuccessType}
import model.points.Points
import sangria.macros.derive._
import sangria.schema._

import scala.reflect.ClassTag

trait ToolGraphQLModels {

  protected val semanticVersionType: ObjectType[Unit, SemanticVersion] = deriveObjectType()

  protected val exerciseInterfaceType: InterfaceType[Unit, Exercise] = InterfaceType(
    "ExerciseInterface",
    () =>
      fields[Unit, Exercise](
        Field("id", IntType, resolve = _.value.id),
        Field("collectionId", IntType, resolve = _.value.collectionId),
        Field("toolId", StringType, resolve = _.value.toolId),
        Field("semanticVersion", semanticVersionType, resolve = _.value.semanticVersion),
        Field("title", StringType, resolve = _.value.title),
        Field("authors", ListType(StringType), resolve = _.value.authors),
        Field("text", StringType, resolve = _.value.text),
        Field("difficulty", OptionType(IntType), resolve = _.value.difficulty)
      )
  ).withPossibleTypes(() => ToolList.tools.map(t => t.graphQlModels.ExerciseType))

  protected val exerciseFileType: ObjectType[Unit, ExerciseFile] = deriveObjectType()

  protected val exerciseFileInputType: InputObjectType[ExerciseFile] = deriveInputObjectType(
    InputObjectTypeName("ExerciseFileInput")
  )

  protected val KeyValueObjectType: ObjectType[Unit, KeyValueObject] = deriveObjectType()

  protected val successTypeType: EnumType[SuccessType] = deriveEnumType()

  protected val pointsType: ObjectType[Unit, Points] = deriveObjectType()

}

trait ToolGraphQLModelBasics[ExerciseType <: Exercise, SolType, PartType <: ExPart] extends ToolGraphQLModels {

  // Sample solution types

  protected def sampleSolutionType[ASolType](
    name: String,
    SolTypeType: OutputType[ASolType]
  ): ObjectType[Unit, SampleSolution[ASolType]] = ObjectType(
    s"${name}SampleSolution",
    fields[Unit, SampleSolution[ASolType]](
      Field("id", IntType, resolve = _.value.id),
      Field("sample", SolTypeType, resolve = _.value.sample)
    )
  )

  protected val stringSampleSolutionType: ObjectType[Unit, SampleSolution[String]] =
    sampleSolutionType("String", StringType)

  // Matching types

  protected val matchTypeType: EnumType[MatchType] = deriveEnumType()

  private def matchingResultInterface[T, M <: Match[T]]: InterfaceType[Unit, MatchingResult[T, M]] = InterfaceType(
    "MatchingResult",
    () =>
      fields(
        Field("points", FloatType, resolve = _.value.points.asDouble),
        Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble),
        Field("allMatches", ListType(newMatchInterface), resolve = _.value.allMatches)
      )
  )

  protected val newMatchInterface: InterfaceType[Unit, Match[_]] = InterfaceType(
    "NewMatch",
    fields[Unit, Match[_]](
      Field("matchType", matchTypeType, resolve = _.value.matchType),
      Field("userArgDescription", OptionType(StringType), resolve = _.value.userArgDescription),
      Field("sampleArgDescription", OptionType(StringType), resolve = _.value.sampleArgDescription)
    )
  )

  protected def buildStringMatchTypeType[T, M <: Match[T]](name: String)(
    implicit _x: ClassTag[M]
  ): ObjectType[Unit, M] = ObjectType(
    name,
    interfaces[Unit, M](newMatchInterface),
    fields[Unit, M](
      Field("sampleArg", OptionType(StringType), resolve = _.value.sampleArgDescription),
      Field("userArg", OptionType(StringType), resolve = _.value.userArgDescription)
    )
  )

  protected def matchingResultType[T, M <: Match[T]](
    name: String,
    mType: OutputType[M]
  ): ObjectType[Unit, MatchingResult[T, M]] = deriveObjectType(
    ObjectTypeName(s"${name}MatchingResult"),
    Interfaces(matchingResultInterface[T, M]),
    ExcludeFields("points", "maxPoints"),
    ReplaceField("allMatches", Field("allMatches", ListType(mType), resolve = _.value.allMatches))
  )

  protected val abstractResultTypeType: InterfaceType[Unit, AbstractCorrectionResult] = InterfaceType(
    "AbstractCorrectionResult",
    fields[Unit, AbstractCorrectionResult](
      Field("solutionSaved", BooleanType, resolve = _.value.solutionSaved),
      Field("points", FloatType, resolve = _.value.points.asDouble),
      Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble)
    )
  )

  val ExerciseType: ObjectType[Unit, ExerciseType]

  val AbstractResultTypeType: OutputType[Any]

  val SolTypeInputType: InputType[SolType]

  val PartTypeInputType: EnumType[PartType]

}