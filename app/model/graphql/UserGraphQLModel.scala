package model.graphql

import model.{MongoClientQueries, TopicWithLevel}
import model.tools.ToolList
import play.modules.reactivemongo.MongoController
import sangria.schema._

import scala.concurrent.Future

trait UserGraphQLModel extends BasicGraphQLModels with GraphQLArguments with MongoClientQueries {
  self: MongoController =>

  protected val userType: ObjectType[Unit, String] = ObjectType(
    "User",
    fields[Unit, String](
      Field(
        "proficiencies",
        ListType(topicWithLevelType),
        arguments = toolIdArgument :: Nil,
        resolve = context =>
          ToolList.tools.find(_.id == context.arg(toolIdArgument)) match {
            case None       => Future.successful(Seq.empty)
            case Some(tool) => allTopicsWithLevelForTool(context.value, tool)
          }
      )
    )
  )

}
