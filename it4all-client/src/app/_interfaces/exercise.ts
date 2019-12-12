export interface ExerciseContent {
  tags?: string[];
}

// Solutions

export interface DbSolution<T> {
  toolId: string;
  collId: number;
  exId: number;
  partId: string;
  solution: T;
}
