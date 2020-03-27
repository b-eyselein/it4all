package model.tools.collectionTools.uml

import model.tools.collectionTools.{KeyValueObject, SampleSolution, ToolGraphQLModelBasics}
import sangria.macros.derive.{ReplaceField, deriveEnumType, deriveObjectType}
import sangria.schema.{EnumType, ListType, ObjectType, Field}

object UmlGraphQLModels extends ToolGraphQLModelBasics[UmlExerciseContent] {

  private val umlVisibilityType: EnumType[UmlVisibility] = deriveEnumType()

  private val umlAttributeType: ObjectType[Unit, UmlAttribute] = {
    implicit val uvt: EnumType[UmlVisibility] = umlVisibilityType

    deriveObjectType()
  }

  private val umlMethodType: ObjectType[Unit, UmlMethod] = {
    implicit val uvt: EnumType[UmlVisibility] = umlVisibilityType

    deriveObjectType()
  }

  private val umlClassType: ObjectType[Unit, UmlClass] = {
    implicit val umlClassTypeType: EnumType[UmlClassType] = deriveEnumType()

    implicit val uat: ObjectType[Unit, UmlAttribute] = umlAttributeType

    implicit val umt: ObjectType[Unit, UmlMethod] = umlMethodType

    deriveObjectType()
  }

  private val umlAssociationType: ObjectType[Unit, UmlAssociation] = {
    implicit val umlAssociationTypeType: EnumType[UmlAssociationType] = deriveEnumType()

    implicit val umlMultiplicityType: EnumType[UmlMultiplicity] = deriveEnumType()

    deriveObjectType()
  }

  private val umlClassDiagramType: ObjectType[Unit, UmlClassDiagram] = {
    implicit val uct: ObjectType[Unit, UmlClass] = umlClassType

    implicit val umlImplementationType: ObjectType[Unit, UmlImplementation] = deriveObjectType()

    implicit val uat: ObjectType[Unit, UmlAssociation] = umlAssociationType

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

}
