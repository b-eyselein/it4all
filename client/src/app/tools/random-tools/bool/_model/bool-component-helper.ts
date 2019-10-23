import {BooleanVariable} from './bool-node';

export abstract class BoolComponentHelper {

  displayAssignmentValue(assignment: Map<string, boolean>, variable: BooleanVariable): string {
    if (assignment.has(variable.variable)) {
      return assignment.get(variable.variable) ? '1' : '0';
    } else {
      return '';
    }
  }

}
