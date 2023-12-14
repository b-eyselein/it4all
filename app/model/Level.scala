package model

import enumeratum.{Enum, EnumEntry}
import model.graphql.GraphQLContext
import sangria.schema.{Field, IntType, ObjectType, StringType, fields}

sealed abstract class Level(val level: Int) extends EnumEntry {

  def pointsForExerciseCompletion: Int = Math.pow(2.toDouble, level.toDouble - 1).toInt

}

object Level extends Enum[Level] {

  case object Beginner     extends Level(1)
  case object Intermediate extends Level(2)
  case object Advanced     extends Level(3)
  case object Expert       extends Level(4)

  val values: IndexedSeq[Level] = findValues

  val queryType: ObjectType[GraphQLContext, Level] = ObjectType(
    "Level",
    fields[GraphQLContext, Level](
      Field("title", StringType, resolve = _.value.entryName),
      Field("levelIndex", IntType, resolve = _.value.level)
    )
  )

}
