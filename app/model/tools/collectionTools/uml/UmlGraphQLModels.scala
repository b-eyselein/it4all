package model.tools.collectionTools.uml

import model.points
import model.tools.collectionTools.{KeyValueObject, SampleSolution, ToolGraphQLModelBasics}
import sangria.macros.derive._
import sangria.schema.{EnumType, EnumValue, Field, InputObjectType, InputType, ListType, ObjectType}

object UmlGraphQLModels
    extends ToolGraphQLModelBasics[UmlExerciseContent, UmlClassDiagram, UmlCompleteResult, UmlExPart] {

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

  private val umlClassDiagramType: ObjectType[Unit, UmlClassDiagram] = {
    implicit val uct: ObjectType[Unit, UmlClass]                            = umlClassType
    implicit val uat: ObjectType[Unit, UmlAssociation]                      = umlAssociationType
    implicit val umlImplementationType: ObjectType[Unit, UmlImplementation] = deriveObjectType()

    deriveObjectType()
  }

  override val ExContentTypeType: ObjectType[Unit, UmlExerciseContent] = {
    implicit val sampleSolType: ObjectType[Unit, SampleSolution[UmlClassDiagram]] =
      sampleSolutionType("Uml", umlClassDiagramType)

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

  override val SolTypeInputType: InputType[UmlClassDiagram] = {
    implicit val ucit: InputObjectType[UmlClass]       = umlClassInputType
    implicit val uait: InputObjectType[UmlAssociation] = umlAssociationInputType
    implicit val uiit: InputObjectType[UmlImplementation] = deriveInputObjectType(
      InputObjectTypeName("UmlImplementationInput")
    )

    deriveInputObjectType[UmlClassDiagram](InputObjectTypeName("UmlClassDiagramInput"))
  }

  // Result types

  override val AbstractResultTypeType: ObjectType[Unit, UmlCompleteResult] = {
    implicit val pt: ObjectType[Unit, points.Points] = pointsType

    deriveObjectType(
      // TODO: include fields...
      ExcludeFields("classResult", "assocResult", "implResult")
    )
  }

  override val PartTypeInputType: EnumType[UmlExPart] = EnumType(
    "UmlExPart",
    values = UmlExParts.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

}
