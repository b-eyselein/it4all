package model

import model.graphql.GraphQLContext
import play.api.libs.json.{Format, Json, OFormat, Reads, Writes}
import sangria.schema.{Field, ObjectType, StringType, fields}

final case class KeyValueObject(key: String, value: String)

object KeyValueObject {

  val jsonFormat: OFormat[KeyValueObject] = Json.format

  val queryType: ObjectType[GraphQLContext, KeyValueObject] = ObjectType(
    "KeyValueObject",
    fields[GraphQLContext, KeyValueObject](
      Field("key", StringType, resolve = _.value.key),
      Field("value", StringType, resolve = _.value.value)
    )
  )

  val mapFormat: Format[Map[String, String]] = Format(
    Reads.seq(KeyValueObject.jsonFormat).map(_.map { case KeyValueObject(key, value) => (key, value) }.toMap),
    Writes.seq(KeyValueObject.jsonFormat).contramap(_.toSeq.map { case (key, value) => KeyValueObject(key, value) })
  )

}
