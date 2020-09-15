package model.tools.flask

import model.graphql.FilesSolutionToolGraphQLModelBasics
import model.{ExerciseFile, FilesSolution, SampleSolution}
import sangria.macros.derive._
import sangria.schema._

object FlaskToolGraphQLModels
    extends FilesSolutionToolGraphQLModelBasics[FlaskExerciseContent, FlaskExPart, FlaskAbstractCorrectionResult] {

  override val partEnumType: EnumType[FlaskExPart] = EnumType(
    "FlaskExercisePart",
    values = FlaskExPart.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

  override val exerciseContentType: ObjectType[Unit, FlaskExerciseContent] = {
    implicit val eft: ObjectType[Unit, ExerciseFile]                  = exerciseFileType
    implicit val sst: ObjectType[Unit, SampleSolution[FilesSolution]] = sampleSolutionType

    deriveObjectType()
  }

  override val toolAbstractResultTypeInterfaceType: InterfaceType[Unit, FlaskAbstractCorrectionResult] =
    InterfaceType(
      "FlaskAbstractCorrectionResult",
      fields[Unit, FlaskAbstractCorrectionResult](
        Field("_x", BooleanType, resolve = _ => false)
      )
    )

}
