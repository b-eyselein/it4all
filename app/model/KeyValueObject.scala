package model

import model.graphql.GraphQLContext
import sangria.schema.{Field, ObjectType, StringType, fields}

final case class KeyValueObject(key: String, value: String)

object KeyValueObject {
  val queryType: ObjectType[GraphQLContext, KeyValueObject] = ObjectType(
    "KeyValueObject",
    fields[GraphQLContext, KeyValueObject](
      Field("key", StringType, resolve = _.value.key),
      Field("value", StringType, resolve = _.value.value)
    )
  )

}
