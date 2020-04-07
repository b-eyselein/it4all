package model.tools.xml

import de.uniwue.dtd.model.{AttributeList, ElementDefinition, ElementLine}
import de.uniwue.dtd.parser.DTDParseException
import model.core.matching.MatchType
import model.core.result.SuccessType
import model.tools.{SampleSolution, ToolGraphQLModelBasics}
import sangria.macros.derive._
import sangria.schema._

object XmlGraphQLModels extends ToolGraphQLModelBasics[XmlExerciseContent, XmlSolution, XmlExPart] {

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

  private val elementDefinitionType: ObjectType[Unit, ElementDefinition] = deriveObjectType(
    ReplaceField("content", Field("content", StringType, resolve = _.value.contentAsString))
  )

  private val attributeListType: ObjectType[Unit, AttributeList] = deriveObjectType(
    ReplaceField(
      "attributeDefinitions",
      Field("attributeDefinitions", ListType(StringType), resolve = _.value.attributeDefinitions.map(_.asString))
    )
  )

  private val elementLineType: ObjectType[Unit, ElementLine] = {
    implicit val edt: ObjectType[Unit, ElementDefinition] = elementDefinitionType
    implicit val alt: ObjectType[Unit, AttributeList]     = attributeListType

    deriveObjectType()
  }

  private val elementLineMatchType: ObjectType[Unit, ElementLineMatch] = {
    implicit val mt: EnumType[MatchType]                           = matchTypeType
    implicit val elt: ObjectType[Unit, ElementLine]                = elementLineType
    implicit val elar: ObjectType[Unit, ElementLineAnalysisResult] = deriveObjectType()

    deriveObjectType(
      Interfaces(newMatchInterface)
    )
  }

  private val xmlGrammarResultType: ObjectType[Unit, XmlGrammarResult] = {
    implicit val dpet: ObjectType[Unit, DTDParseException] = dtdParseExceptionType
    implicit val elct: ObjectType[Unit, XmlToolMain.ElementLineComparison] =
      matchingResultType[ElementLine, ElementLineMatch]("XmlElementLineComparison", elementLineMatchType)

    deriveObjectType()
  }

  private val xmlCompleteResultType: ObjectType[Unit, XmlCompleteResult] = {
    implicit val st: EnumType[SuccessType]                = successTypeType
    implicit val xet: ObjectType[Unit, XmlError]          = xmlErrorType
    implicit val xgrt: ObjectType[Unit, XmlGrammarResult] = xmlGrammarResultType

    deriveObjectType[Unit, XmlCompleteResult](
      Interfaces(abstractResultTypeType),
      ExcludeFields("solutionSaved", "points", "maxPoints")
    )
  }

  override val AbstractResultTypeType: OutputType[Any] = xmlCompleteResultType

  override val PartTypeInputType: EnumType[XmlExPart] = EnumType(
    "XmlExPart",
    values = XmlExParts.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

}
