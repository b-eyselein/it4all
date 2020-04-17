package model.tools

import model.core.matching.{Match, MatchType, MatchingResult}
import model.core.result.{AbstractCorrectionResult, SuccessType}
import model.json.KeyValueObject
import model.points.Points
import sangria.macros.derive._
import sangria.schema._

import scala.reflect.ClassTag

trait ToolGraphQLModels {

  protected val toolStateType: EnumType[ToolState] = deriveEnumType()

  private val topicsType: ObjectType[Unit, Topic] = deriveObjectType()

  protected val exerciseContentUnionType: UnionType[Unit] = UnionType(
    "ExerciseContent",
    types = () =>
      ToolList.tools.map(t => {
        println(t.graphQlModels)
        println(t.graphQlModels.exerciseContentType)
        t.graphQlModels.exerciseContentType
      })
  )

  protected val exerciseInterfaceType: InterfaceType[Unit, Exercise[_, _]] = InterfaceType(
    "ExerciseInterface",
    () =>
      fields[Unit, Exercise[_, _]](
        Field("id", IntType, resolve = _.value.id),
        Field("collectionId", IntType, resolve = _.value.collectionId),
        Field("toolId", StringType, resolve = _.value.toolId),
        Field("title", StringType, resolve = _.value.title),
        Field("authors", ListType(StringType), resolve = _.value.authors),
        Field("text", StringType, resolve = _.value.text),
        Field("difficulty", OptionType(IntType), resolve = _.value.difficulty),
        Field("topics", ListType(topicsType), resolve = _.value.topics),
        Field("content", exerciseContentUnionType, resolve = _.value.content)
      )
  ).withPossibleTypes(() => ToolList.tools.map(_.graphQlModels.exerciseType))

  protected val exerciseFileType: ObjectType[Unit, ExerciseFile] = deriveObjectType()

  protected val exerciseFileInputType: InputObjectType[ExerciseFile] = deriveInputObjectType(
    InputObjectTypeName("ExerciseFileInput")
  )

  protected val KeyValueObjectType: ObjectType[Unit, KeyValueObject] = deriveObjectType()

  protected val successTypeType: EnumType[SuccessType] = deriveEnumType()

  protected val pointsType: ObjectType[Unit, Points] = deriveObjectType()

}

trait ToolGraphQLModelBasics[SolType, ContentType, ExType <: Exercise[SolType, ContentType], PartType <: ExPart]
    extends ToolGraphQLModels {

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

  val exerciseContentType: ObjectType[Unit, ContentType]

  val exerciseType: ObjectType[Unit, ExType]

  val AbstractResultTypeType: OutputType[Any]

  val SolTypeInputType: InputType[SolType]

  val PartTypeInputType: EnumType[PartType]

}
