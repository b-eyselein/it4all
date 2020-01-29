package model.tools.collectionTools.uml

import model.core.result.CompleteResult
import model.points._
import model.tools.collectionTools.uml.UmlToolMain.{AssociationComparison, ClassComparison, ImplementationComparison}


final case class UmlCompleteResult(
  classResult: Option[ClassComparison],
  assocResult: Option[AssociationComparison],
  implResult: Option[ImplementationComparison],
  points: Points,
  maxPoints: Points,
  solutionSaved: Boolean
) extends CompleteResult
