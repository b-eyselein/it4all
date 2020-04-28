package model.graphql

import model.lesson.Lesson
import sangria.macros.derive.{ExcludeFields, deriveObjectType}
import sangria.schema.ObjectType

object LessonGraphQLModel {
  /*

  private val LessonContentType: InterfaceType[Unit, LessonContent] = InterfaceType(
    "LessonContent",
    fields[Unit, LessonContent](
      Field("id", IntType, resolve = _.value.id)
    )
  )

  val LessonTextContentType: ObjectType[Unit, LessonTextContent] = deriveObjectType(
    Interfaces(LessonContentType)
  )
   */

  val LessonType: ObjectType[Unit, Lesson] = {

    //    implicit val lct: ListType[LessonContent] = ListType(LessonContentType)

    deriveObjectType(
      ExcludeFields("content")
    )
  }

}
