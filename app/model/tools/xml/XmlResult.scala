package model.tools.xml

import de.uniwue.dtd.parser.DTDParseException
import model.core.result.{CompleteResult, CompleteResultJsonProtocol, EvaluationResult}
import model.tools.xml.XmlConsts._
import play.api.libs.functional.syntax._
import play.api.libs.json.{Writes, _}

trait XmlEvaluationResult extends EvaluationResult

trait XmlCompleteResult extends CompleteResult[XmlEvaluationResult]


