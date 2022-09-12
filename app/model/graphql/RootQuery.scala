package model.graphql

import model.tools.{Tool, ToolList}
import sangria.schema.{Argument, Field, ListType, ObjectType, StringType, fields}

trait RootQuery extends ToolQuery with GraphQLBasics with GraphQLArguments {

  protected val toolIdArgument: Argument[String] = Argument("toolId", StringType)

  private val resolveTool: Resolver[Unit, Tool] = context => {
    val toolId = context.arg(toolIdArgument)

    futureFromOption(
      ToolList.tools.find(_.id == toolId),
      new Exception(s"No such tool with id $toolId")
    )
  }

  protected val queryType: ObjectType[GraphQLContext, Unit] = ObjectType(
    "Query",
    fields[GraphQLContext, Unit](
      Field("tools", ListType(toolType), resolve = _ => ToolList.tools),
      Field("tool", toolType, arguments = toolIdArgument :: Nil, resolve = resolveTool)
    )
  )

}
