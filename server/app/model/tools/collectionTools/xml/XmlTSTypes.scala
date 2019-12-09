package model.tools.collectionTools.xml

import model.tools.collectionTools.{SampleSolution, ToolTSInterfaceTypes}
import nl.codestar.scalatsi.{TSIType, TSType}
import nl.codestar.scalatsi.TypescriptType.TSInterface

trait XmlTSTypes extends ToolTSInterfaceTypes {

  implicit val xmlExerciseContentTSI: TSIType[XmlExerciseContent] = {
    implicit val xsst: TSIType[SampleSolution[XmlSolution]] = sampleSolutionTSI[XmlSolution](TSType.fromCaseClass[XmlSolution])

    TSType.fromCaseClass[XmlExerciseContent]
  }

}
