package model.graphql

import model.tools._
import model.tools.programming.{ProgrammingExerciseContent, ProgrammingGraphQLModels}
import model.tools.regex.{RegexExerciseContent, RegexGraphQLModels}
import model.tools.sql.{SqlExerciseContent, SqlGraphQLModels}
import model.tools.uml.{UmlExerciseContent, UmlGraphQLModels}
import model.tools.web.{WebExerciseContent, WebGraphQLModels}
import model.tools.xml.{XmlExerciseContent, XmlGraphQLModels}
import sangria.macros.derive.{AddFields, ExcludeFields, deriveObjectType}
import sangria.schema.{BooleanType, Field, IDType, ListType, ObjectType, OptionType, StringType, fields}

import scala.concurrent.ExecutionContext

trait ExerciseGraphQLModels extends BasicGraphQLModels with GraphQLArguments {

  protected type UntypedExercise = Exercise[_, _ <: ExerciseContent[_]]

  protected implicit val ec: ExecutionContext

  protected val exPartType: ObjectType[Unit, ExPart] = ObjectType(
    "ExPart",
    fields[Unit, ExPart](
      Field("id", StringType, resolve = _.value.id),
      Field("name", StringType, resolve = _.value.partName),
      Field("isEntryPart", BooleanType, resolve = _.value.isEntryPart)
    )
  )

  protected val exerciseType: ObjectType[GraphQLContext, UntypedExercise] = deriveObjectType(
    ExcludeFields("content"),
    AddFields(
      Field(
        "completeId",
        IDType,
        resolve = context => s"${context.value.toolId}_${context.value.collectionId}_${context.value.exerciseId}"
      ),
      Field(
        "topics",
        ListType(topicType),
        resolve = context =>
          ToolList.tools.find(_.id == context.value.toolId) match {
            case None       => ???
            case Some(tool) => tool.allTopics.filter(t => context.value.topicAbbreviations.contains(t.abbreviation))
          }
      ),
      Field(
        "programmingContent",
        OptionType(ProgrammingGraphQLModels.exerciseContentType),
        resolve = _.value.content match {
          case x: ProgrammingExerciseContent => Some(x)
          case _                             => None
        }
      ),
      Field("regexContent", OptionType(RegexGraphQLModels.exerciseContentType), resolve = _.value.content match {
        case x: RegexExerciseContent => Some(x)
        case _                       => None
      }),
      Field("sqlContent", OptionType(SqlGraphQLModels.exerciseContentType), resolve = _.value.content match {
        case x: SqlExerciseContent => Some(x)
        case _                     => None
      }),
      Field("umlContent", OptionType(UmlGraphQLModels.exerciseContentType), resolve = _.value.content match {
        case x: UmlExerciseContent => Some(x)
        case _                     => None
      }),
      Field("webContent", OptionType(WebGraphQLModels.exerciseContentType), resolve = _.value.content match {
        case x: WebExerciseContent => Some(x)
        case _                     => None
      }),
      Field("xmlContent", OptionType(XmlGraphQLModels.exerciseContentType), resolve = _.value.content match {
        case x: XmlExerciseContent => Some(x)
        case _                     => None
      }),
      Field("parts", ListType(exPartType), resolve = context => context.value.content.parts)
    )
  )

}
