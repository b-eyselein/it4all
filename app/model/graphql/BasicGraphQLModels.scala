package model.graphql

import model._
import model.result.SuccessType
import sangria.macros.derive._
import sangria.schema._

trait BasicGraphQLModels {

  private val levelType: ObjectType[Unit, Level] = ObjectType(
    "Level",
    fields[Unit, Level](
      Field("title", StringType, resolve = _.value.entryName),
      Field("levelIndex", IntType, resolve = _.value.level)
    )
  )

  private val topicType: ObjectType[Unit, Topic] = {
    implicit val lt: ObjectType[Unit, Level] = levelType

    deriveObjectType()
  }

  protected val topicWithLevelType: ObjectType[Unit, TopicWithLevel] = {
    implicit val tt: ObjectType[Unit, Topic] = topicType
    implicit val lt: ObjectType[Unit, Level] = levelType

    deriveObjectType()
  }

  protected val userProficiencyType: ObjectType[Unit, UserProficiency] = {
    implicit val tt: ObjectType[Unit, Topic] = topicType

    deriveObjectType(
      AddFields(
        Field("points", IntType, resolve = _.value.getPoints),
        Field("pointsForNextLevel", IntType, resolve = _.value.pointsForNextLevel),
        Field("level", levelType, resolve = _.value.getLevel)
      ),
      ExcludeFields("pointsForExercises")
    )
  }

  protected val KeyValueObjectType: ObjectType[Unit, KeyValueObject] = deriveObjectType()

  protected val successTypeType: EnumType[SuccessType] = deriveEnumType()

}
