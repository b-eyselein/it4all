package model.graphql

import model.{JsonProtocols, RegisterValues, UserCredentials}
import play.api.libs.json.OFormat
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.playJson._
import sangria.schema.{Argument, InputObjectType, IntType, StringType}

trait GraphQLArguments {

  protected val toolIdArgument: Argument[String] = Argument("toolId", StringType)

  protected val collIdArgument: Argument[Int] = Argument("collId", IntType)

  protected val exIdArgument: Argument[Int] = Argument("exId", IntType)

  protected val partIdArgument: Argument[String] = Argument("partId", StringType)

  protected val ltiUuidArgument: Argument[String] = Argument("ltiUuid", StringType)

  protected val userCredentialsArgument: Argument[UserCredentials] = {
    implicit val ucf: OFormat[UserCredentials]                     = JsonProtocols.userCredentialsFormat
    val userCredentialsInputType: InputObjectType[UserCredentials] = deriveInputObjectType()

    Argument("credentials", userCredentialsInputType)
  }

  protected val registerValuesArgument: Argument[RegisterValues] = {
    implicit val rvf: OFormat[RegisterValues]                    = JsonProtocols.registerValuesFormat
    val registerValuesInputType: InputObjectType[RegisterValues] = deriveInputObjectType()

    Argument("registerValues", registerValuesInputType)
  }

}
