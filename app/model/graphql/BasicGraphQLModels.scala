package model.graphql

import model._
import model.core.result.SuccessType
import model.json.KeyValueObject
import model.tools.ToolState
import sangria.macros.derive._
import sangria.schema._

trait BasicGraphQLModels {

  private val loggedInUserType: ObjectType[Unit, LoggedInUser] = deriveObjectType()

  protected val loggedInUserWithTokenType: ObjectType[Unit, LoggedInUserWithToken] = {
    implicit val liut: ObjectType[Unit, LoggedInUser] = loggedInUserType

    deriveObjectType()
  }

  protected val toolStateType: EnumType[ToolState] = deriveEnumType()

  protected val levelEnumType: EnumType[Level] = deriveEnumType()

  protected val topicType: ObjectType[Unit, Topic] = {
    implicit val lt: EnumType[Level] = levelEnumType

    deriveObjectType()
  }

  protected val topicWithLevelType: ObjectType[Unit, TopicWithLevel] = {
    implicit val tt: ObjectType[Unit, Topic] = topicType
    implicit val lt: EnumType[Level]         = levelEnumType

    deriveObjectType()
  }

  protected val exerciseFileType: ObjectType[Unit, ExerciseFile] = deriveObjectType()

  protected val exerciseFileInputType: InputObjectType[ExerciseFile] = deriveInputObjectType(
    InputObjectTypeName("ExerciseFileInput")
  )

  protected val KeyValueObjectType: ObjectType[Unit, KeyValueObject] = deriveObjectType()

  protected val successTypeType: EnumType[SuccessType] = deriveEnumType()

}
