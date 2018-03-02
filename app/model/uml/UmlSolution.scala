package model.uml

import model.PartSolution

case class UmlSolution(username: String, exerciseId: Int, part: UmlExPart,
                       classes: Seq[UmlCompleteClass],
                       associations: Seq[UmlAssociation],
                       implementations: Seq[UmlImplementation]) extends PartSolution {

  override type PartType = UmlExPart

}
