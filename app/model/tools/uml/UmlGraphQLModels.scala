package model.tools.uml

import model.SampleSolution
import model.core.matching.{MatchType, MatchingResult}
import model.graphql.{GraphQLArguments, ToolGraphQLModelBasics}
import model.json.KeyValueObject
import model.tools.uml.UmlTool.{AssociationComparison, ClassComparison, ImplementationComparison}
import model.tools.uml.matcher._
import play.api.libs.json.OFormat
import sangria.macros.derive._
import sangria.marshalling.playJson._
import sangria.schema._

object UmlGraphQLModels
    extends ToolGraphQLModelBasics[UmlClassDiagram, UmlExerciseContent, UmlExPart, UmlAbstractResult]
    with GraphQLArguments {

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

    implicit val uaf: OFormat[UmlAttribute] = UmlClassDiagramJsonFormat.umlAttributeFormat
    implicit val umf: OFormat[UmlMethod]    = UmlClassDiagramJsonFormat.umlMethodFormat

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

  override val sampleSolutionType: ObjectType[Unit, SampleSolution[UmlClassDiagram]] =
    buildSampleSolutionType("Uml", umlClassDiagramType)

  override val exerciseContentType: ObjectType[Unit, UmlExerciseContent] = {
    implicit val sst: ObjectType[Unit, SampleSolution[UmlClassDiagram]] = sampleSolutionType

    deriveObjectType(
      AddFields(
        Field(
          "part",
          OptionType(partEnumType),
          arguments = partIdArgument :: Nil,
          resolve = context => UmlExPart.values.find(_.id == context.arg(partIdArgument))
        )
      ),
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

  override val SolTypeInputType: InputType[UmlClassDiagram] = {
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

    deriveObjectType(
      Interfaces(newMatchInterface)
    )
  }

  private val umlMethodAnalysisResultType: ObjectType[Unit, UmlMethodAnalysisResult] = {
    implicit val uvt: EnumType[UmlVisibility] = umlVisibilityType

    deriveObjectType()
  }

  private val umlMethodMatchType: ObjectType[Unit, UmlMethodMatch] = {
    implicit val mt: EnumType[MatchType]                          = matchTypeType
    implicit val umt: ObjectType[Unit, UmlMethod]                 = umlMethodType
    implicit val umart: ObjectType[Unit, UmlMethodAnalysisResult] = umlMethodAnalysisResultType

    deriveObjectType(
      Interfaces(newMatchInterface)
    )
  }

  private val umlClassMatchAnalysisResultType: ObjectType[Unit, UmlClassMatchAnalysisResult] = {
    implicit val uctt: EnumType[UmlClassType] = umlClassTypeType

    implicit val uact: ObjectType[Unit, MatchingResult[UmlAttribute, UmlAttributeMatch]] =
      matchingResultType("UmlAttribute", umlAttributeMatchType)
    implicit val umct: ObjectType[Unit, MatchingResult[UmlMethod, UmlMethodMatch]] =
      matchingResultType("UmlMethod", umlMethodMatchType)

    deriveObjectType()
  }

  private val umlClassMatchType: ObjectType[Unit, UmlClassMatch] = {
    implicit val mt: EnumType[MatchType]                               = matchTypeType
    implicit val uct: ObjectType[Unit, UmlClass]                       = umlClassType
    implicit val ucmart: ObjectType[Unit, UmlClassMatchAnalysisResult] = umlClassMatchAnalysisResultType

    deriveObjectType(
      Interfaces(newMatchInterface)
    )
  }

  private val umlAssociationAnalysisResultType: ObjectType[Unit, UmlAssociationAnalysisResult] = {
    implicit val uatt: EnumType[UmlAssociationType] = umlAssociationTypeType

    deriveObjectType()
  }

  private val umlAssociationMatchType: ObjectType[Unit, UmlAssociationMatch] = {
    implicit val mt: EnumType[MatchType]                               = matchTypeType
    implicit val uat: ObjectType[Unit, UmlAssociation]                 = umlAssociationType
    implicit val uaart: ObjectType[Unit, UmlAssociationAnalysisResult] = umlAssociationAnalysisResultType

    deriveObjectType(
      Interfaces(newMatchInterface)
    )
  }

  private val umlImplementationMatchType: ObjectType[Unit, UmlImplementationMatch] = {
    implicit val mt: EnumType[MatchType]                  = matchTypeType
    implicit val uit: ObjectType[Unit, UmlImplementation] = umlImplementationType

    deriveObjectType(
      Interfaces(newMatchInterface)
    )
  }

  // Abstract result

  private val umlAbstractResultType = InterfaceType(
    "UmlAbstractResult",
    fields[Unit, UmlAbstractResult](
      Field("solutionSaved", BooleanType, resolve = _.value.solutionSaved),
      Field("points", FloatType, resolve = _.value.points.asDouble),
      Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble)
    )
  ).withPossibleTypes(() => List(umlResultType, umlInternalErrorResultType))

  private val umlResultType: ObjectType[Unit, UmlResult] = {
    implicit val cct: ObjectType[Unit, ClassComparison] =
      matchingResultType("UmlClass", umlClassMatchType)
    implicit val act: ObjectType[Unit, AssociationComparison] =
      matchingResultType("UmlAssociation", umlAssociationMatchType)
    implicit val ict: ObjectType[Unit, ImplementationComparison] =
      matchingResultType("UmlImplementation", umlImplementationMatchType)

    deriveObjectType[Unit, UmlResult](
      Interfaces(umlAbstractResultType),
      ExcludeFields("solutionSaved", "points", "maxPoints")
    )
  }

  private val umlInternalErrorResultType: ObjectType[Unit, UmlInternalErrorResult] = deriveObjectType(
    Interfaces(umlAbstractResultType),
    ExcludeFields("maxPoints")
  )

  override val toolAbstractResultTypeInterfaceType: InterfaceType[Unit, UmlAbstractResult] = umlAbstractResultType

}
