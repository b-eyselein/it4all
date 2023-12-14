package model.graphql

import sangria.schema.{Argument, IntType, StringType}

object GraphQLArguments {
  val toolIdArgument: Argument[String] = Argument("toolId", StringType)
  val collIdArgument: Argument[Int]    = Argument("collId", IntType)
  val exIdArgument: Argument[Int]      = Argument("exId", IntType)
}
