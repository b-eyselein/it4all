import {booleanVariable, BooleanVariable} from './bool-node';
import {Assignment} from './assignment';

export const sampleVariable: BooleanVariable = booleanVariable('z');
export const learnerVariable: BooleanVariable = booleanVariable('y');

export function displayAssignmentValue(assignment: Assignment, variable: BooleanVariable): string {
  if (variable.variable in assignment) {
    return assignment[variable.variable] ? '1' : '0';
  } else {
    return '';
  }
}

export function isCorrect(assignment: Assignment): boolean {
  return assignment[learnerVariable.variable] === assignment[sampleVariable.variable];
}
