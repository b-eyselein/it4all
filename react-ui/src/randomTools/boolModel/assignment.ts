import {BooleanVariable} from './bool-node';

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
