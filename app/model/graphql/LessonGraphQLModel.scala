package model.graphql

import model._
import model.mongo.MongoClientQueries
import sangria.macros.derive._
import sangria.schema._

trait LessonGraphQLModel extends GraphQLArguments with MongoClientQueries {

  private val lessonContentType: InterfaceType[Unit, LessonContent] = InterfaceType(
    "LessonContent",
    fields[Unit, LessonContent](
      Field("contentId", IntType, resolve = _.value.contentId),
      Field("lessonId", IntType, resolve = _.value.lessonId),
      Field("toolId", StringType, resolve = _.value.toolId)
    )
  ).withPossibleTypes(() => List(lessonTextContentType, lessonMultipleChoiceQuestionsContentType))

  private val lessonTextContentType: ObjectType[Unit, LessonTextContent] = deriveObjectType(
    Interfaces(lessonContentType)
  )

  private val lessonMultipleChoiceQuestionAnswerType: ObjectType[Unit, LessonMultipleChoiceQuestionAnswer] =
    deriveObjectType()

  private val lessonMultipleChoiceQuestionType: ObjectType[Unit, LessonMultipleChoiceQuestion] = {
    implicit val lmcqat: ObjectType[Unit, LessonMultipleChoiceQuestionAnswer] = lessonMultipleChoiceQuestionAnswerType

    deriveObjectType()
  }

  private val lessonMultipleChoiceQuestionsContentType: ObjectType[Unit, LessonMultipleChoiceQuestionsContent] = {
    implicit val lmcqt: ObjectType[Unit, LessonMultipleChoiceQuestion] = lessonMultipleChoiceQuestionType

    deriveObjectType(
      Interfaces(lessonContentType)
    )
  }

  val lessonType: ObjectType[GraphQLContext, Lesson] = ObjectType(
    "Lesson",
    fields[GraphQLContext, Lesson](
      Field("lessonId", IntType, resolve = _.value.lessonId),
      Field("toolId", StringType, resolve = _.value.toolId),
      Field("title", StringType, resolve = _.value.title),
      Field("description", StringType, resolve = _.value.description),
      Field("video", OptionType(StringType), resolve = _.value.video),
      Field(
        "contentCount",
        LongType,
        resolve = context => futureLessonContentCountForLesson(context.value.toolId, context.value.lessonId)
      ),
      Field(
        "contents",
        ListType(lessonContentType),
        resolve = context => futureLessonContentsForLesson(context.value.toolId, context.value.lessonId)
      ),
      Field(
        "content",
        OptionType(lessonContentType),
        arguments = lessonIdArgument :: Nil,
        resolve = context => futureLessonContentById(context.value.toolId, context.value.lessonId, context.arg(lessonIdArgument))
      )
    )
  )

}
