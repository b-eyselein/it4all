package model.tools.flask

import model.graphql.FilesSolutionToolGraphQLModelBasics
import model.{FilesSolution, SampleSolution}
import sangria.macros.derive._
import sangria.schema._

object FlaskToolGraphQLModels
    extends FilesSolutionToolGraphQLModelBasics[FlaskExerciseContent, FlaskExPart, FlaskCorrectionResult] {

  override val partEnumType: EnumType[FlaskExPart] = EnumType(
    "FlaskExercisePart",
    values = FlaskExPart.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

  override val exerciseContentType: ObjectType[Unit, FlaskExerciseContent] = {
    implicit val sst: ObjectType[Unit, SampleSolution[FilesSolution]] = sampleSolutionType

    deriveObjectType(
      AddFields(
        Field("_x", BooleanType, resolve = _ => false)
      )
    )
  }

  private val flaskAbstractCorrectionResultType: InterfaceType[Unit, FlaskCorrectionResult] = InterfaceType(
    "FlaskAbstractCorrectionResult",
    fields[Unit, FlaskCorrectionResult](
      Field("_x", BooleanType, resolve = _ => false)
    )
  )

  override val toolAbstractResultTypeInterfaceType: InterfaceType[Unit, FlaskCorrectionResult] =
    flaskAbstractCorrectionResultType

}
