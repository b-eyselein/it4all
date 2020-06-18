package model.graphql

import model.tools.Helper.UntypedExercise
import model.tools.programming.{ProgrammingExerciseContent, ProgrammingGraphQLModels}
import model.tools.regex.{RegexExerciseContent, RegexGraphQLModels}
import model.tools.sql.{SqlExerciseContent, SqlGraphQLModels}
import model.tools.uml.{UmlExerciseContent, UmlGraphQLModels}
import model.tools.web.{WebExerciseContent, WebGraphQLModels}
import model.tools.xml.{XmlExerciseContent, XmlGraphQLModels}
import model.{ExPart, LoggedInUser}
import sangria.schema._

trait ExerciseGraphQLModels extends BasicGraphQLModels with GraphQLArguments {

  private val exPartType: ObjectType[Unit, ExPart] = ObjectType(
    "ExPart",
    fields[Unit, ExPart](
      Field("id", StringType, resolve = _.value.id),
      Field("name", StringType, resolve = _.value.partName),
      Field("isEntryPart", BooleanType, resolve = _.value.isEntryPart)
    )
  )

  protected val exerciseType: ObjectType[Unit, (LoggedInUser, UntypedExercise)] = ObjectType(
    "Exercise",
    fields[Unit, (LoggedInUser, UntypedExercise)](
      Field("exerciseId", IntType, resolve = _.value._2.exerciseId),
      Field("collectionId", IntType, resolve = _.value._2.collectionId),
      Field("toolId", StringType, resolve = _.value._2.toolId),
      Field("title", StringType, resolve = _.value._2.title),
      Field("authors", ListType(StringType), resolve = _.value._2.authors),
      Field("text", StringType, resolve = _.value._2.text),
      Field("topicsWithLevels", ListType(topicWithLevelType), resolve = _.value._2.topicsWithLevels),
      Field("difficulty", IntType, resolve = _.value._2.difficulty),
      // Content
      Field(
        "programmingContent",
        OptionType(ProgrammingGraphQLModels.exerciseContentType),
        resolve = _.value._2.content match {
          case x: ProgrammingExerciseContent => Some(x)
          case _                             => None
        }
      ),
      Field(
        "regexContent",
        OptionType(RegexGraphQLModels.exerciseContentType),
        resolve = _.value._2.content match {
          case x: RegexExerciseContent => Some(x)
          case _                       => None
        }
      ),
      Field(
        "sqlContent",
        OptionType(SqlGraphQLModels.exerciseContentType),
        resolve = _.value._2.content match {
          case x: SqlExerciseContent => Some(x)
          case _                     => None
        }
      ),
      Field(
        "umlContent",
        OptionType(UmlGraphQLModels.exerciseContentType),
        resolve = _.value._2.content match {
          case x: UmlExerciseContent => Some(x)
          case _                     => None
        }
      ),
      Field(
        "webContent",
        OptionType(WebGraphQLModels.exerciseContentType),
        resolve = _.value._2.content match {
          case x: WebExerciseContent => Some(x)
          case _                     => None
        }
      ),
      Field(
        "xmlContent",
        OptionType(XmlGraphQLModels.exerciseContentType),
        resolve = _.value._2.content match {
          case x: XmlExerciseContent => Some(x)
          case _                     => None
        }
      ),
      Field("parts", ListType(exPartType), resolve = context => context.value._2.content.parts)
      /* ,
      // TODO: has user solved this exercise?
      Field("solved", BooleanType, resolve = context => false)
       */
    )
  )

}
