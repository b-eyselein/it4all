package model.graphql

import model.{LoggedInUser, LoggedInUserWithToken}
import model.core.result.SuccessType
import model.json.KeyValueObject
import model.tools.{ExerciseFile, ToolState, Topic}
import sangria.macros.derive.{InputObjectTypeName, deriveEnumType, deriveInputObjectType, deriveObjectType}
import sangria.schema.{EnumType, InputObjectType, ObjectType}

trait BasicGraphQLModels {

  private val loggedInUserType: ObjectType[Unit, LoggedInUser] = deriveObjectType()

  protected val loggedInUserWithTokenType: ObjectType[Unit, LoggedInUserWithToken] = {
    implicit val liut: ObjectType[Unit, LoggedInUser] = loggedInUserType

    deriveObjectType()
  }

  protected val toolStateType: EnumType[ToolState] = deriveEnumType()

  protected val topicType: ObjectType[Unit, Topic] = deriveObjectType()

  protected val exerciseFileType: ObjectType[Unit, ExerciseFile] = deriveObjectType()

  protected val exerciseFileInputType: InputObjectType[ExerciseFile] = deriveInputObjectType(
    InputObjectTypeName("ExerciseFileInput")
  )

  protected val KeyValueObjectType: ObjectType[Unit, KeyValueObject] = deriveObjectType()

  protected val successTypeType: EnumType[SuccessType] = deriveEnumType()

}
