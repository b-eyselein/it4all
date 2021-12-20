package model.graphql

import model.{RegisterValues, UserCredentials}
import play.api.libs.json.{Json, OFormat}
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.playJson._
import sangria.schema.{Argument, InputObjectType, IntType, StringType}

trait GraphQLArguments {

  protected val toolIdArgument: Argument[String] = Argument("toolId", StringType)

  protected val collIdArgument: Argument[Int] = Argument("collId", IntType)

  protected val exIdArgument: Argument[Int] = Argument("exId", IntType)

  protected val partIdArgument: Argument[String] = Argument("partId", StringType)

  protected val ltiUuidArgument: Argument[String] = Argument("ltiUuid", StringType)

  // Login

  private val userCredentialsFormat: OFormat[UserCredentials] = Json.format

  protected val userCredentialsArgument: Argument[UserCredentials] = {
    implicit val ucf: OFormat[UserCredentials]                     = userCredentialsFormat
    val userCredentialsInputType: InputObjectType[UserCredentials] = deriveInputObjectType()

    Argument("credentials", userCredentialsInputType)
  }

  // Registration

  private val registerValuesFormat: OFormat[RegisterValues] = Json.format

  protected val registerValuesArgument: Argument[RegisterValues] = {
    implicit val rvf: OFormat[RegisterValues]                    = registerValuesFormat
    val registerValuesInputType: InputObjectType[RegisterValues] = deriveInputObjectType()

    Argument[RegisterValues]("registerValues", registerValuesInputType)
  }

}
