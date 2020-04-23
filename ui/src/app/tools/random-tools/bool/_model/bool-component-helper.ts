import {BooleanVariable} from './bool-node';
import {BooleanFormula} from './bool-formula';

export abstract class BoolComponentHelper {

  readonly sampleVariable: BooleanVariable = new BooleanVariable('z');
  readonly learnerVariable: BooleanVariable = new BooleanVariable('y');

  formula: BooleanFormula;
  // FIXME: remove assignments, get from formula!
  assignments: Map<string, boolean>[] = [];

  corrected = false;
  completelyCorrect = false;

  displayAssignmentValue(assignment: Map<string, boolean>, variable: BooleanVariable): string {
    if (assignment.has(variable.variable)) {
      return assignment.get(variable.variable) ? '1' : '0';
    } else {
      return '';
    }
  }

  isCorrect(assignment: Map<string, boolean>): boolean {
    return assignment.get(this.learnerVariable.variable) === assignment.get(this.sampleVariable.variable);
  }

}
