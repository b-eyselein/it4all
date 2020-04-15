package model.tools.uml

import model.core.result.AbstractCorrectionResult
import model.points._
import model.tools.uml.UmlTool.{AssociationComparison, ClassComparison, ImplementationComparison}

final case class UmlCompleteResult(
  classResult: Option[ClassComparison],
  assocResult: Option[AssociationComparison],
  implResult: Option[ImplementationComparison],
  points: Points,
  maxPoints: Points,
  solutionSaved: Boolean
) extends AbstractCorrectionResult
