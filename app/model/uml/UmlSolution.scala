package model.uml

import model.Solution

case class UmlSolution(username: String, exerciseId: Int, forPart: UmlExPart,
                       classes: Seq[UmlCompleteClass],
                       associations: Seq[UmlAssociation],
                       implementations: Seq[UmlImplementation]) extends Solution
