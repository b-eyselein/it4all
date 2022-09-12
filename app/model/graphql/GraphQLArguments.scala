package model.graphql

import sangria.schema.{Argument, IntType}

trait GraphQLArguments {

  protected val collIdArgument: Argument[Int] = Argument("collId", IntType)

  protected val exIdArgument: Argument[Int] = Argument("exId", IntType)

}
