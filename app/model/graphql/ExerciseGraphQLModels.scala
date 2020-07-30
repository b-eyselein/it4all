package model.graphql

import model.mongo.MongoExercisePartResultQueries
import model.tools.Helper.UntypedExercise
import model.tools.Tool
import model.tools.programming.{ProgrammingExerciseContent, ProgrammingGraphQLModels}
import model.tools.regex.{RegexExerciseContent, RegexGraphQLModels}
import model.tools.sql.{SqlExerciseContent, SqlGraphQLModels}
import model.tools.uml.{UmlExerciseContent, UmlGraphQLModels}
import model.tools.web.{WebExerciseContent, WebGraphQLModels}
import model.tools.xml.{XmlExerciseContent, XmlGraphQLModels}
import model.{ExPart, LoggedInUser}
import sangria.schema._

trait ExerciseGraphQLModels extends BasicGraphQLModels with GraphQLArguments {
  self: MongoExercisePartResultQueries =>

  private val exPartType: ObjectType[Unit, (UntypedExercise, ExPart)] = ObjectType(
    "ExPart",
    fields[Unit, (UntypedExercise, ExPart)](
      Field("id", StringType, resolve = _.value._2.id),
      Field("name", StringType, resolve = _.value._2.partName),
      Field("isEntryPart", BooleanType, resolve = _.value._2.isEntryPart),
      Field(
        "solved",
        BooleanType,
        resolve = context => {
          val exercise = context.value._1

          futureExerciseResultById(exercise.toolId, exercise.collectionId, exercise.exerciseId, context.value._2.id)
            .map {
              case None                          => false
              case Some(basicExercisePartResult) => basicExercisePartResult.isCorrect
            }
        }
      )
    )
  )

  protected val exerciseType: ObjectType[Unit, (LoggedInUser, Tool, UntypedExercise)] = ObjectType(
    "Exercise",
    fields[Unit, (LoggedInUser, Tool, UntypedExercise)](
      Field("exerciseId", IntType, resolve = _.value._3.exerciseId),
      Field("collectionId", IntType, resolve = _.value._3.collectionId),
      Field("toolId", StringType, resolve = _.value._3.toolId),
      Field("title", StringType, resolve = _.value._3.title),
      Field("authors", ListType(StringType), resolve = _.value._3.authors),
      Field("text", StringType, resolve = _.value._3.text),
      Field("topicsWithLevels", ListType(topicWithLevelType), resolve = _.value._3.topicsWithLevels),
      Field("difficulty", IntType, resolve = _.value._3.difficulty),
      // Content
      Field(
        "programmingContent",
        OptionType(ProgrammingGraphQLModels.exerciseContentType),
        resolve = _.value._3.content match {
          case x: ProgrammingExerciseContent => Some(x)
          case _                             => None
        }
      ),
      Field(
        "regexContent",
        OptionType(RegexGraphQLModels.exerciseContentType),
        resolve = _.value._3.content match {
          case x: RegexExerciseContent => Some(x)
          case _                       => None
        }
      ),
      Field(
        "sqlContent",
        OptionType(SqlGraphQLModels.exerciseContentType),
        resolve = _.value._3.content match {
          case x: SqlExerciseContent => Some(x)
          case _                     => None
        }
      ),
      Field(
        "umlContent",
        OptionType(UmlGraphQLModels.exerciseContentType),
        resolve = _.value._3.content match {
          case x: UmlExerciseContent => Some(x)
          case _                     => None
        }
      ),
      Field(
        "webContent",
        OptionType(WebGraphQLModels.exerciseContentType),
        resolve = _.value._3.content match {
          case x: WebExerciseContent => Some(x)
          case _                     => None
        }
      ),
      Field(
        "xmlContent",
        OptionType(XmlGraphQLModels.exerciseContentType),
        resolve = _.value._3.content match {
          case x: XmlExerciseContent => Some(x)
          case _                     => None
        }
      ),
      Field(
        "parts",
        ListType(exPartType),
        resolve = context => context.value._3.content.parts.map { exPart => (context.value._3, exPart) }
      )
    )
  )

}
