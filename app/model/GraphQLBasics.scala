package model

import sangria.schema.{Argument, IntType, StringType}

trait GraphQLBasics {

  // Arguments

  protected val toolIdArgument: Argument[String] = Argument("toolId", StringType)

  protected val lessonIdArgument: Argument[Int] = Argument("lessonId", IntType)

  protected val collIdArgument: Argument[Int] = Argument("collId", IntType)

  protected val exIdArgument: Argument[Int] = Argument("exId", IntType)

  protected val contentArgument: Argument[String] = Argument("content", StringType)

}
