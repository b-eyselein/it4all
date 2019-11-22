export interface ExerciseCollection {
  id: number;
  toolId: string;
  title: string;
  author: string;
  text: string;
  state: string;
  shortName: string;

  // FIXME: other solution?
  exercises: Exercise<any>[];
}

export interface SemanticVersion {
  major: number;
  minor: number;
  patch: number;
}

export interface ExerciseContent {
  tags?: string[];
}

export interface Exercise<EC extends ExerciseContent> {
  id: number;
  collectionId: number;
  toolId: string;
  semanticVersion: SemanticVersion;

  title: string;
  author: string;
  text: string;
  state: string;

  content: EC;
}

// Solutions

export interface DbSolution<T> {
  toolId: string;
  collId: number;
  exId: number;
  partId: string;
  solution: T;
}
