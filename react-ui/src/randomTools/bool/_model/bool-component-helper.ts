import {BooleanVariable} from './bool-node';

export const sampleVariable: BooleanVariable = new BooleanVariable('z');
export const learnerVariable: BooleanVariable = new BooleanVariable('y');

export function displayAssignmentValue(assignment: Map<string, boolean>, variable: BooleanVariable): string {
  if (assignment.has(variable.variable)) {
    return assignment.get(variable.variable) ? '1' : '0';
  } else {
    return '';
  }
}

export function isCorrect(assignment: Map<string, boolean>): boolean {
  return assignment.get(learnerVariable.variable) === assignment.get(sampleVariable.variable);
}
