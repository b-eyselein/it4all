package model.tools.collectionTools.xml

import de.uniwue.dtd.parser.DTDParseException
import model.core.result.SuccessType
import model.points.Points
import model.tools.collectionTools.{SampleSolution, ToolGraphQLModelBasics}
import sangria.macros.derive._
import sangria.schema.{EnumType, InputType, ObjectType}

object XmlGraphQLModels extends ToolGraphQLModelBasics[XmlExerciseContent, XmlSolution, XmlCompleteResult] {

  private val xmlSolutionType: ObjectType[Unit, XmlSolution] = deriveObjectType()

  override val ExContentTypeType: ObjectType[Unit, XmlExerciseContent] = {
    implicit val sst: ObjectType[Unit, SampleSolution[XmlSolution]] = sampleSolutionType("Xml", xmlSolutionType)

    deriveObjectType()
  }

  // Solution types

  override val SolTypeInputType: InputType[XmlSolution] =
    deriveInputObjectType[XmlSolution](InputObjectTypeName("XmlSolutionInput"))

  // Result types

  private val xmlErrorTypeType: EnumType[XmlErrorType] = deriveEnumType()

  private val xmlErrorType: ObjectType[Unit, XmlError] = {
    implicit val st: EnumType[SuccessType]    = successTypeType
    implicit val xett: EnumType[XmlErrorType] = xmlErrorTypeType

    deriveObjectType()
  }

  private val dtdParseExceptionType: ObjectType[Unit, DTDParseException] = deriveObjectType()

  private val xmlGrammarResultType: ObjectType[Unit, XmlGrammarResult] = {
    implicit val dpet: ObjectType[Unit, DTDParseException] = dtdParseExceptionType

    deriveObjectType(
      // TODO: include fields!
      ExcludeFields("results")
    )
  }

  override val CompResultTypeType: ObjectType[Unit, XmlCompleteResult] = {
    implicit val st: EnumType[SuccessType]                = successTypeType
    implicit val xet: ObjectType[Unit, XmlError]          = xmlErrorType
    implicit val xgrt: ObjectType[Unit, XmlGrammarResult] = xmlGrammarResultType
    implicit val pt: ObjectType[Unit, Points]             = pointsType

    deriveObjectType()
  }

}
