package model.tools.uml

import model.core.result.{AbstractCorrectionResult, InternalErrorResult}
import model.points._
import model.tools.uml.UmlTool.{AssociationComparison, ClassComparison, ImplementationComparison}

trait UmlAbstractResult extends AbstractCorrectionResult

final case class UmlInternalErrorResult(
  msg: String,
  solutionSaved: Boolean,
  maxPoints: Points
) extends UmlAbstractResult
    with InternalErrorResult {

  override def points: Points = zeroPoints

}

final case class UmlResult(
  classResult: Option[ClassComparison],
  assocResult: Option[AssociationComparison],
  implResult: Option[ImplementationComparison],
  points: Points,
  maxPoints: Points,
  solutionSaved: Boolean
) extends UmlAbstractResult
