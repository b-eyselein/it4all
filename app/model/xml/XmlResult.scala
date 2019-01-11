package model.xml

import model.core.JsonWriteable
import model.core.result.{CompleteResult, EvaluationResult}

trait XmlEvaluationResult extends EvaluationResult with JsonWriteable

trait XmlCompleteResult extends CompleteResult[XmlEvaluationResult]
