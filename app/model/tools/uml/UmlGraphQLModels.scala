package model.tools.uml

import model.KeyValueObject
import model.graphql.ToolWithPartsGraphQLModel
import model.matching.{MatchType, MatchingResult}
import model.tools.uml.UmlTool.{AssociationComparison, ClassComparison, ImplementationComparison}
import model.tools.uml.matcher._
import play.api.libs.json.OFormat
import sangria.macros.derive._
import sangria.marshalling.playJson._
import sangria.schema._

import scala.annotation.unused

object UmlGraphQLModels extends ToolWithPartsGraphQLModel[UmlClassDiagram, UmlExerciseContent, UmlResult, UmlExPart] {

  override val partEnumType: EnumType[UmlExPart] = EnumType(
    "UmlExPart",
    values = UmlExPart.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

  private val umlVisibilityType: EnumType[UmlVisibility] = deriveEnumType()
  private val umlClassTypeType: EnumType[UmlClassType]   = deriveEnumType()

  private val umlAssociationTypeType: EnumType[UmlAssociationType] = deriveEnumType()
  private val umlMultiplicityType: EnumType[UmlMultiplicity]       = deriveEnumType()

  private val umlAttributeType: ObjectType[Unit, UmlAttribute] = {
    @unused implicit val uvt: EnumType[UmlVisibility] = umlVisibilityType
    deriveObjectType()
  }

  private val umlAttributeInputType: InputObjectType[UmlAttribute] = {
    @unused implicit val uvt: EnumType[UmlVisibility] = umlVisibilityType

    deriveInputObjectType(InputObjectTypeName("UmlAttributeInput"))
  }

  private val umlMethodType: ObjectType[Unit, UmlMethod] = {
    @unused implicit val uvt: EnumType[UmlVisibility] = umlVisibilityType
    deriveObjectType()
  }

  private val umlMethodInputType: InputObjectType[UmlMethod] = {
    @unused implicit val uvt: EnumType[UmlVisibility] = umlVisibilityType
    deriveInputObjectType(InputObjectTypeName("UmlMethodInput"))
  }

  private val umlClassType: ObjectType[Unit, UmlClass] = {
    @unused implicit val uctt: EnumType[UmlClassType]        = umlClassTypeType
    @unused implicit val uat: ObjectType[Unit, UmlAttribute] = umlAttributeType
    @unused implicit val umt: ObjectType[Unit, UmlMethod]    = umlMethodType

    deriveObjectType()
  }

  private val umlClassInputType: InputObjectType[UmlClass] = {
    @unused implicit val uctt: EnumType[UmlClassType]       = umlClassTypeType
    @unused implicit val uat: InputObjectType[UmlAttribute] = umlAttributeInputType
    @unused implicit val umt: InputObjectType[UmlMethod]    = umlMethodInputType

    @unused implicit val uaf: OFormat[UmlAttribute] = UmlToolJsonProtocol.umlAttributeFormat
    @unused implicit val umf: OFormat[UmlMethod]    = UmlToolJsonProtocol.umlMethodFormat

    deriveInputObjectType(InputObjectTypeName("UmlClassInput"))
  }

  private val umlAssociationType: ObjectType[Unit, UmlAssociation] = {
    @unused implicit val uatt: EnumType[UmlAssociationType] = umlAssociationTypeType
    @unused implicit val umt: EnumType[UmlMultiplicity]     = umlMultiplicityType

    deriveObjectType()
  }

  private val umlAssociationInputType: InputObjectType[UmlAssociation] = {
    @unused implicit val uatt: EnumType[UmlAssociationType] = umlAssociationTypeType
    @unused implicit val umt: EnumType[UmlMultiplicity]     = umlMultiplicityType

    deriveInputObjectType(InputObjectTypeName("UmlAssociationInput"))
  }

  private val umlImplementationType: ObjectType[Unit, UmlImplementation] = deriveObjectType()

  private val umlClassDiagramType: ObjectType[Unit, UmlClassDiagram] = {
    @unused implicit val uct: ObjectType[Unit, UmlClass]          = umlClassType
    @unused implicit val uat: ObjectType[Unit, UmlAssociation]    = umlAssociationType
    @unused implicit val uit: ObjectType[Unit, UmlImplementation] = umlImplementationType

    deriveObjectType()
  }

  override val exerciseContentType: ObjectType[Unit, UmlExerciseContent] = {
    @unused implicit val sst: ObjectType[Unit, UmlClassDiagram] = umlClassDiagramType

    deriveObjectType(
      ReplaceField(
        "mappings",
        Field(
          "mappings",
          ListType(KeyValueObject.queryType),
          resolve = context => context.value.mappings.map { case (key, value) => KeyValueObject(key, value) }.toList
        )
      )
    )
  }

  // Solution types

  override val solutionInputType: InputType[UmlClassDiagram] = {
    @unused implicit val ucit: InputObjectType[UmlClass]       = umlClassInputType
    @unused implicit val uait: InputObjectType[UmlAssociation] = umlAssociationInputType
    @unused implicit val uiit: InputObjectType[UmlImplementation] = deriveInputObjectType(
      InputObjectTypeName("UmlImplementationInput")
    )

    deriveInputObjectType[UmlClassDiagram](InputObjectTypeName("UmlClassDiagramInput"))
  }

  // Result types

  private val umlAttributeAnalysisResultType: ObjectType[Unit, UmlAttributeAnalysisResult] = {
    @unused implicit val uvt: EnumType[UmlVisibility] = umlVisibilityType

    deriveObjectType()
  }

  private val umlAttributeMatchType: ObjectType[Unit, UmlAttributeMatch] = {
    @unused implicit val mt: EnumType[MatchType]                             = matchTypeType
    @unused implicit val uat: ObjectType[Unit, UmlAttribute]                 = umlAttributeType
    @unused implicit val uaart: ObjectType[Unit, UmlAttributeAnalysisResult] = umlAttributeAnalysisResultType

    deriveObjectType()
  }

  private val umlMethodAnalysisResultType: ObjectType[Unit, UmlMethodAnalysisResult] = {
    @unused implicit val uvt: EnumType[UmlVisibility] = umlVisibilityType

    deriveObjectType()
  }

  private val umlMethodMatchType: ObjectType[Unit, UmlMethodMatch] = {
    @unused implicit val mt: EnumType[MatchType]                          = matchTypeType
    @unused implicit val umt: ObjectType[Unit, UmlMethod]                 = umlMethodType
    @unused implicit val umart: ObjectType[Unit, UmlMethodAnalysisResult] = umlMethodAnalysisResultType

    deriveObjectType()
  }

  private val umlClassMatchAnalysisResultType: ObjectType[Unit, UmlClassMatchAnalysisResult] = {
    @unused implicit val uctt: EnumType[UmlClassType] = umlClassTypeType

    @unused implicit val uact: ObjectType[Unit, MatchingResult[UmlAttribute, UmlAttributeMatch]] =
      matchingResultType("UmlAttribute", umlAttributeMatchType, umlAttributeType, identity)

    @unused implicit val umct: ObjectType[Unit, MatchingResult[UmlMethod, UmlMethodMatch]] =
      matchingResultType("UmlMethod", umlMethodMatchType, umlMethodType, identity)

    deriveObjectType()
  }

  private val umlClassMatchType: ObjectType[Unit, UmlClassMatch] = {
    @unused implicit val mt: EnumType[MatchType]                               = matchTypeType
    @unused implicit val uct: ObjectType[Unit, UmlClass]                       = umlClassType
    @unused implicit val ucmart: ObjectType[Unit, UmlClassMatchAnalysisResult] = umlClassMatchAnalysisResultType

    deriveObjectType()
  }

  private val umlAssociationAnalysisResultType: ObjectType[Unit, UmlAssociationAnalysisResult] = {
    @unused implicit val uatt: EnumType[UmlAssociationType] = umlAssociationTypeType

    deriveObjectType()
  }

  private val umlAssociationMatchType: ObjectType[Unit, UmlAssociationMatch] = {
    @unused implicit val mt: EnumType[MatchType]                               = matchTypeType
    @unused implicit val uat: ObjectType[Unit, UmlAssociation]                 = umlAssociationType
    @unused implicit val uaart: ObjectType[Unit, UmlAssociationAnalysisResult] = umlAssociationAnalysisResultType

    deriveObjectType()
  }

  private val umlImplementationMatchType: ObjectType[Unit, UmlImplementationMatch] = {
    @unused implicit val mt: EnumType[MatchType]                  = matchTypeType
    @unused implicit val uit: ObjectType[Unit, UmlImplementation] = umlImplementationType

    deriveObjectType()
  }

  override val resultType: OutputType[UmlResult] = {
    @unused implicit val cct: ObjectType[Unit, ClassComparison] =
      matchingResultType("UmlClass", umlClassMatchType, umlClassType, identity)

    @unused implicit val act: ObjectType[Unit, AssociationComparison] =
      matchingResultType("UmlAssociation", umlAssociationMatchType, umlAssociationType, identity)

    @unused implicit val ict: ObjectType[Unit, ImplementationComparison] =
      matchingResultType("UmlImplementation", umlImplementationMatchType, umlImplementationType, identity)

    deriveObjectType[Unit, UmlResult](
      ReplaceField("points", Field("points", FloatType, resolve = _.value.points.asDouble)),
      ReplaceField("maxPoints", Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble))
    )
  }

}
