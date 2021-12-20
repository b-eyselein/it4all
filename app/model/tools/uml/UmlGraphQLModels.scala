package model.tools.uml

import model.KeyValueObject
import model.graphql.{GraphQLArguments, ToolGraphQLModelBasics}
import model.matching.{MatchType, MatchingResult}
import model.tools.uml.UmlTool.{AssociationComparison, ClassComparison, ImplementationComparison}
import model.tools.uml.matcher._
import play.api.libs.json.OFormat
import sangria.macros.derive._
import sangria.schema._
import sangria.marshalling.playJson._

object UmlGraphQLModels extends ToolGraphQLModelBasics[UmlClassDiagram, UmlExerciseContent, UmlExPart, UmlResult] with GraphQLArguments {

  override val partEnumType: EnumType[UmlExPart] = EnumType(
    "UmlExPart",
    values = UmlExPart.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

  private val umlVisibilityType: EnumType[UmlVisibility] = deriveEnumType()
  private val umlClassTypeType: EnumType[UmlClassType]   = deriveEnumType()

  private val umlAssociationTypeType: EnumType[UmlAssociationType] = deriveEnumType()
  private val umlMultiplicityType: EnumType[UmlMultiplicity]       = deriveEnumType()

  private val umlAttributeType: ObjectType[Unit, UmlAttribute] = {
    implicit val uvt: EnumType[UmlVisibility] = umlVisibilityType
    deriveObjectType()
  }

  private val umlAttributeInputType: InputObjectType[UmlAttribute] = {
    implicit val uvt: EnumType[UmlVisibility] = umlVisibilityType

    deriveInputObjectType(InputObjectTypeName("UmlAttributeInput"))
  }

  private val umlMethodType: ObjectType[Unit, UmlMethod] = {
    implicit val uvt: EnumType[UmlVisibility] = umlVisibilityType
    deriveObjectType()
  }

  private val umlMethodInputType: InputObjectType[UmlMethod] = {
    implicit val uvt: EnumType[UmlVisibility] = umlVisibilityType
    deriveInputObjectType(InputObjectTypeName("UmlMethodInput"))
  }

  private val umlClassType: ObjectType[Unit, UmlClass] = {
    implicit val uctt: EnumType[UmlClassType]        = umlClassTypeType
    implicit val uat: ObjectType[Unit, UmlAttribute] = umlAttributeType
    implicit val umt: ObjectType[Unit, UmlMethod]    = umlMethodType

    deriveObjectType()
  }

  private val umlClassInputType: InputObjectType[UmlClass] = {
    implicit val uctt: EnumType[UmlClassType]       = umlClassTypeType
    implicit val uat: InputObjectType[UmlAttribute] = umlAttributeInputType
    implicit val umt: InputObjectType[UmlMethod]    = umlMethodInputType

    implicit val uaf: OFormat[UmlAttribute] = UmlToolJsonProtocol.umlAttributeFormat
    implicit val umf: OFormat[UmlMethod]    = UmlToolJsonProtocol.umlMethodFormat

    deriveInputObjectType(InputObjectTypeName("UmlClassInput"))
  }

  private val umlAssociationType: ObjectType[Unit, UmlAssociation] = {
    implicit val uatt: EnumType[UmlAssociationType] = umlAssociationTypeType
    implicit val umt: EnumType[UmlMultiplicity]     = umlMultiplicityType

    deriveObjectType()
  }

  private val umlAssociationInputType: InputObjectType[UmlAssociation] = {
    implicit val uatt: EnumType[UmlAssociationType] = umlAssociationTypeType
    implicit val umt: EnumType[UmlMultiplicity]     = umlMultiplicityType

    deriveInputObjectType(InputObjectTypeName("UmlAssociationInput"))
  }

  private val umlImplementationType: ObjectType[Unit, UmlImplementation] = deriveObjectType()

  private val umlClassDiagramType: ObjectType[Unit, UmlClassDiagram] = {
    implicit val uct: ObjectType[Unit, UmlClass]          = umlClassType
    implicit val uat: ObjectType[Unit, UmlAssociation]    = umlAssociationType
    implicit val uit: ObjectType[Unit, UmlImplementation] = umlImplementationType

    deriveObjectType()
  }

  override val exerciseContentType: ObjectType[Unit, UmlExerciseContent] = {
    implicit val sst: ObjectType[Unit, UmlClassDiagram] = umlClassDiagramType

    deriveObjectType(
      ReplaceField(
        "mappings",
        Field(
          "mappings",
          ListType(KeyValueObjectType),
          resolve = context => context.value.mappings.map { case (key, value) => KeyValueObject(key, value) }.toList
        )
      )
    )
  }

  // Solution types

  override val solutionInputType: InputType[UmlClassDiagram] = {
    implicit val ucit: InputObjectType[UmlClass]       = umlClassInputType
    implicit val uait: InputObjectType[UmlAssociation] = umlAssociationInputType
    implicit val uiit: InputObjectType[UmlImplementation] = deriveInputObjectType(
      InputObjectTypeName("UmlImplementationInput")
    )

    deriveInputObjectType[UmlClassDiagram](InputObjectTypeName("UmlClassDiagramInput"))
  }

  // Result types

  private val umlAttributeAnalysisResultType: ObjectType[Unit, UmlAttributeAnalysisResult] = {
    implicit val uvt: EnumType[UmlVisibility] = umlVisibilityType

    deriveObjectType()
  }

  private val umlAttributeMatchType: ObjectType[Unit, UmlAttributeMatch] = {
    implicit val mt: EnumType[MatchType]                             = matchTypeType
    implicit val uat: ObjectType[Unit, UmlAttribute]                 = umlAttributeType
    implicit val uaart: ObjectType[Unit, UmlAttributeAnalysisResult] = umlAttributeAnalysisResultType

    deriveObjectType()
  }

  private val umlMethodAnalysisResultType: ObjectType[Unit, UmlMethodAnalysisResult] = {
    implicit val uvt: EnumType[UmlVisibility] = umlVisibilityType

    deriveObjectType()
  }

  private val umlMethodMatchType: ObjectType[Unit, UmlMethodMatch] = {
    implicit val mt: EnumType[MatchType]                          = matchTypeType
    implicit val umt: ObjectType[Unit, UmlMethod]                 = umlMethodType
    implicit val umart: ObjectType[Unit, UmlMethodAnalysisResult] = umlMethodAnalysisResultType

    deriveObjectType()
  }

  private val umlClassMatchAnalysisResultType: ObjectType[Unit, UmlClassMatchAnalysisResult] = {
    implicit val uctt: EnumType[UmlClassType] = umlClassTypeType

    implicit val uact: ObjectType[Unit, MatchingResult[UmlAttribute, UmlAttributeMatch]] =
      matchingResultType("UmlAttribute", umlAttributeMatchType, umlAttributeType, identity)

    implicit val umct: ObjectType[Unit, MatchingResult[UmlMethod, UmlMethodMatch]] =
      matchingResultType("UmlMethod", umlMethodMatchType, umlMethodType, identity)

    deriveObjectType()
  }

  private val umlClassMatchType: ObjectType[Unit, UmlClassMatch] = {
    implicit val mt: EnumType[MatchType]                               = matchTypeType
    implicit val uct: ObjectType[Unit, UmlClass]                       = umlClassType
    implicit val ucmart: ObjectType[Unit, UmlClassMatchAnalysisResult] = umlClassMatchAnalysisResultType

    deriveObjectType()
  }

  private val umlAssociationAnalysisResultType: ObjectType[Unit, UmlAssociationAnalysisResult] = {
    implicit val uatt: EnumType[UmlAssociationType] = umlAssociationTypeType

    deriveObjectType()
  }

  private val umlAssociationMatchType: ObjectType[Unit, UmlAssociationMatch] = {
    implicit val mt: EnumType[MatchType]                               = matchTypeType
    implicit val uat: ObjectType[Unit, UmlAssociation]                 = umlAssociationType
    implicit val uaart: ObjectType[Unit, UmlAssociationAnalysisResult] = umlAssociationAnalysisResultType

    deriveObjectType()
  }

  private val umlImplementationMatchType: ObjectType[Unit, UmlImplementationMatch] = {
    implicit val mt: EnumType[MatchType]                  = matchTypeType
    implicit val uit: ObjectType[Unit, UmlImplementation] = umlImplementationType

    deriveObjectType()
  }

  override val resultType: OutputType[UmlResult] = {
    implicit val cct: ObjectType[Unit, ClassComparison] =
      matchingResultType("UmlClass", umlClassMatchType, umlClassType, identity)

    implicit val act: ObjectType[Unit, AssociationComparison] =
      matchingResultType("UmlAssociation", umlAssociationMatchType, umlAssociationType, identity)

    implicit val ict: ObjectType[Unit, ImplementationComparison] =
      matchingResultType("UmlImplementation", umlImplementationMatchType, umlImplementationType, identity)

    deriveObjectType[Unit, UmlResult](
      ReplaceField("points", Field("points", FloatType, resolve = _.value.points.asDouble)),
      ReplaceField("maxPoints", Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble))
    )
  }

}
