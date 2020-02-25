package model.typescriptTypes

import model.adaption.{Proficiencies, ToolProficiency, TopicProficiency}
import nl.codestar.scalatsi.{DefaultTSTypes, TSIType, TSType}

object ProficiencyTsTypes extends DefaultTSTypes{

  private implicit val toolProficiencyTSI: TSIType[ToolProficiency] = TSType.fromCaseClass

  private implicit val topicProficiencyTSI: TSIType[TopicProficiency] = TSType.fromCaseClass

  val proficienciesTSI: TSIType[Proficiencies] = TSType.fromCaseClass

}
