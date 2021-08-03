import {Assignment, BooleanVariable} from './bool-node';

export const sampleVariable: BooleanVariable = new BooleanVariable('z');
export const learnerVariable: BooleanVariable = new BooleanVariable('y');

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
