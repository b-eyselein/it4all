package model.graphql

import model.tools._
import model.tools.programming.{ProgrammingExerciseContent, ProgrammingGraphQLModels}
import model.tools.regex.{RegexExerciseContent, RegexGraphQLModels}
import model.tools.sql.{SqlExerciseContent, SqlGraphQLModels}
import model.tools.uml.{UmlExerciseContent, UmlGraphQLModels}
import model.tools.web.{WebExerciseContent, WebGraphQLModels}
import model.tools.xml.{XmlExerciseContent, XmlGraphQLModels}
import sangria.macros.derive.{AddFields, ExcludeFields, ReplaceField, deriveObjectType}
import sangria.schema.{BooleanType, Field, ListType, ObjectType, OptionType, StringType, fields}

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
    ReplaceField(
      "topicAbbreviations",
      Field(
        "topics",
        ListType(topicWithLevelType),
        resolve = context => {
          val allTopics = ToolList.tools
            .find(_.id == context.value.toolId)
            .map(_.allTopics)
            .getOrElse(Seq.empty)

          context.value.topicAbbreviations.flatMap {
            case (topicAbbreviation, level) =>
              allTopics
                .find { topic => topicAbbreviation == topic.abbreviation }
                .map { topic => TopicWithLevel(topic, level) }
          }
        }
      )
    ),
    AddFields(
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
