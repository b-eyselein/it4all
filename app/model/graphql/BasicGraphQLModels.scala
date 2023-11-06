package model.graphql

import model._
import sangria.macros.derive._
import sangria.schema._

import scala.annotation.unused

trait BasicGraphQLModels {

  protected val levelType: ObjectType[Unit, Level] = ObjectType(
    "Level",
    fields[Unit, Level](
      Field("title", StringType, resolve = _.value.entryName),
      Field("levelIndex", IntType, resolve = _.value.level)
    )
  )

  private val topicType: ObjectType[Unit, Topic] = {
    @unused implicit val lt: ObjectType[Unit, Level] = levelType

    deriveObjectType()
  }

  protected val topicWithLevelType: ObjectType[Unit, TopicWithLevel] = {
    @unused implicit val tt: ObjectType[Unit, Topic] = topicType
    @unused implicit val lt: ObjectType[Unit, Level] = levelType

    deriveObjectType()
  }

  protected val userProficiencyType: ObjectType[Unit, UserProficiency] = {
    @unused implicit val tt: ObjectType[Unit, Topic] = topicType

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

}
