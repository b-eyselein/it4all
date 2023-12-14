package model.graphql

import model.tools.{Tool, ToolList}
import sangria.schema._

trait RootQuery extends ToolQuery with GraphQLBasics {

  private val resolveTool: Resolver[Unit, Option[Tool]] = context => {
    val toolId = context.arg(GraphQLArguments.toolIdArgument)
    ToolList.tools.find { _.id == toolId }
  }

  protected val queryType: ObjectType[GraphQLContext, Unit] = ObjectType(
    "Query",
    fields[GraphQLContext, Unit](
      Field("tools", ListType(toolType), resolve = _ => ToolList.tools),
      Field("tool", OptionType(toolType), arguments = GraphQLArguments.toolIdArgument :: Nil, resolve = resolveTool)
    )
  )

}
