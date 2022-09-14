import {BooleanNode, booleanVariable, BooleanVariable, evaluate} from './boolNode';

export type Assignment = { [key: string]: boolean };

export function calculateAssignments(variables: BooleanVariable[]): Assignment[] {
  let assignments: Assignment[] = [];

  for (const variable of variables) {
    if (assignments.length === 0) {
      assignments = [
        {[variable.variable]: false},
        {[variable.variable]: true}
      ];
    } else {
      assignments = assignments.flatMap(
        (assignment: Assignment) => [
          {...assignment, [variable.variable]: false},
          {...assignment, [variable.variable]: true}
        ]
      );
    }
  }

  return assignments;
}

export const sampleVariable: BooleanVariable = booleanVariable('z');
export const learnerVariable: BooleanVariable = booleanVariable('y');

export function displayAssignmentValue(assignment: Assignment, variable: BooleanNode): string {
  return evaluate(variable, assignment) ? '1' : '0';
}

export function isCorrect(assignment: Assignment): boolean {
  return assignment[learnerVariable.variable] === assignment[sampleVariable.variable];
}
