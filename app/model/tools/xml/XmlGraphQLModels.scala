package model.tools.xml

import de.uniwue.dtd.model.{AttributeList, ElementDefinition, ElementLine}
import de.uniwue.dtd.parser.DTDParseException
import model.graphql.{GraphQLArguments, ToolWithPartsGraphQLModel}
import model.matching.MatchType
import sangria.macros.derive._
import sangria.schema._

object XmlGraphQLModels extends ToolWithPartsGraphQLModel[XmlSolution, XmlExerciseContent, XmlResult, XmlExPart] with GraphQLArguments {

  override val partEnumType: EnumType[XmlExPart] = EnumType(
    "XmlExPart",
    values = XmlExPart.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

  private val xmlSolutionType: ObjectType[Unit, XmlSolution] = deriveObjectType()

  override val exerciseContentType: ObjectType[Unit, XmlExerciseContent] = {
    implicit val sst: ObjectType[Unit, XmlSolution] = xmlSolutionType

    deriveObjectType()
  }

  // Solution types

  override val solutionInputType: InputType[XmlSolution] =
    deriveInputObjectType[XmlSolution](InputObjectTypeName("XmlSolutionInput"))

  // Result types

  private val xmlErrorTypeType: EnumType[XmlErrorType] = deriveEnumType()

  private val xmlErrorType: ObjectType[Unit, XmlError] = {
    implicit val xett: EnumType[XmlErrorType] = xmlErrorTypeType

    deriveObjectType()
  }

  private val xmlDocumentResultType: ObjectType[Unit, XmlDocumentResult] = {
    implicit val xet: ObjectType[Unit, XmlError] = xmlErrorType

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

    deriveObjectType()
  }

  private val xmlGrammarResultType: ObjectType[Unit, XmlGrammarResult] = {
    implicit val dpet: ObjectType[Unit, DTDParseException] = dtdParseExceptionType

    implicit val elct: ObjectType[Unit, XmlTool.ElementLineComparison] =
      matchingResultType("XmlElementLineComparison", elementLineMatchType, elementLineType, identity)

    deriveObjectType()
  }

  // Abstract result

  override val resultType: OutputType[XmlResult] = {
    implicit val xdrt: ObjectType[Unit, XmlDocumentResult] = xmlDocumentResultType
    implicit val xgrt: ObjectType[Unit, XmlGrammarResult]  = xmlGrammarResultType

    deriveObjectType[Unit, XmlResult](
      ReplaceField("points", Field("points", FloatType, resolve = _.value.points.asDouble)),
      ReplaceField("maxPoints", Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble))
    )
  }

}
