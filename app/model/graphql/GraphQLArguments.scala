package model.graphql

import sangria.schema.{Argument, IntType, StringType}

trait GraphQLArguments {
  protected val toolIdArgument: Argument[String] = Argument("toolId", StringType)
  protected val collIdArgument: Argument[Int]    = Argument("collId", IntType)
  protected val exIdArgument: Argument[Int]      = Argument("exId", IntType)
}
