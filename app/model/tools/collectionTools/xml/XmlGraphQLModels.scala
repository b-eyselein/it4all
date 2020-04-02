package model.tools.collectionTools.xml

import de.uniwue.dtd.model.ElementLine
import de.uniwue.dtd.parser.DTDParseException
import model.core.matching.{MatchType, MatchingResult}
import model.core.result.SuccessType
import model.tools.collectionTools.{SampleSolution, ToolGraphQLModelBasics}
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

  private val elementLineType: ObjectType[Unit, ElementLine] = ObjectType(
    "ElementLine",
    fields[Unit, ElementLine](
      Field("todo", OptionType(IntType), resolve = _ => None)
    )
  )
  /* {
    implicit val edt: ObjectType[Unit, ElementDefinition] = ???

    deriveObjectType()
  }
   */

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
    implicit val elct: ObjectType[Unit, MatchingResult[ElementLine, ElementLineMatch]] =
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
