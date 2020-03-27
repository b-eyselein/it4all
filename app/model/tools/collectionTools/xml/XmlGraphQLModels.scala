package model.tools.collectionTools.xml

import model.tools.collectionTools.{SampleSolution, ToolGraphQLModelBasics}
import sangria.macros.derive.{InputObjectTypeName, deriveInputObjectType, deriveObjectType}
import sangria.schema.{InputType, ObjectType}

object XmlGraphQLModels extends ToolGraphQLModelBasics[XmlExerciseContent, XmlSolution] {

  private val xmlSolutionType: ObjectType[Unit, XmlSolution] = deriveObjectType()

  override val ExContentTypeType: ObjectType[Unit, XmlExerciseContent] = {
    implicit val sst: ObjectType[Unit, SampleSolution[XmlSolution]] = sampleSolutionType("Xml", xmlSolutionType)

    deriveObjectType()
  }

  override val SolTypeInputType: InputType[XmlSolution] =
    deriveInputObjectType[XmlSolution](InputObjectTypeName("XmlSolutionInput"))

}
