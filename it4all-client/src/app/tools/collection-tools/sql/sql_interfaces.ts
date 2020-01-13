
export interface ISampleSolution {
  id: number;
  sample: object;
}


export interface ISqlRow {
  cells: { [ key: string ]: ISqlCell };
}


export interface ISqlExerciseContent {
  exerciseType: SqlExerciseType;
  hint?: string;
  sampleSolutions: ISampleSolution[];
}

export type SqlExerciseType = ("SELECT" | "CREATE" | "UPDATE" | "INSERT" | "DELETE");

export interface ISqlCell {
  colName: string;
  content: string;
}


export interface ISqlQueryResult {
  columnNames: string[];
  rows: ISqlRow[];
  tableName: string;
}
