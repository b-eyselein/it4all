package model.tools.collectionTools.xml

import model.tools.collectionTools.{SampleSolution, ToolGraphQLModelBasics}
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType

object XmlGraphQLModels extends ToolGraphQLModelBasics[XmlExerciseContent] {

  private val xmlSolutionType: ObjectType[Unit, XmlSolution] = deriveObjectType()

  override val ExContentTypeType: ObjectType[Unit, XmlExerciseContent] = {
    implicit val sst: ObjectType[Unit, SampleSolution[XmlSolution]] = sampleSolutionType(xmlSolutionType)

    deriveObjectType()
  }

}
