package model.graphql

import model.tools.{Tool, ToolList}
import sangria.schema._

trait RootQuery extends ToolQuery with GraphQLBasics with GraphQLArguments {

  private val resolveTool: Resolver[Unit, Option[Tool]] = context => ToolList.tools.find { _.id == context.arg(toolIdArgument) }

  protected val queryType: ObjectType[GraphQLContext, Unit] = ObjectType(
    "Query",
    fields[GraphQLContext, Unit](
      Field("tools", ListType(toolType), resolve = _ => ToolList.tools),
      Field("tool", OptionType(toolType), arguments = toolIdArgument :: Nil, resolve = resolveTool)
    )
  )

}
