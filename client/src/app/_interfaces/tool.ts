export type ToolStatus = 'live' | 'alpha' | 'beta';

export type SuccessType = 'ERROR' | 'NONE' | 'PARTIALLY' | 'COMPLETE';

export interface ToolPart {
  id: string;
  name: string;
  disabled?: boolean;
}

export interface ExerciseTag {
  title: string;
  label: string;
}

export abstract class Tool {
  constructor(
    public id: string,
    public name: string,
    // FIXME: empty if only one part ?!?
    public parts?: ToolPart[],
    public status?: ToolStatus,
    public hasPlayground?: boolean,
    public hasLivePreview?: boolean,
    public isDisabled?: boolean
  ) {
  }

  exerciseHasPart(exercise: Exercise, part: ToolPart): boolean {
    return true;
  }

  abstract processTagString(tag: string): ExerciseTag;

}


export interface ExerciseCollection {
  id: number;
  toolId: string;
  title: string;
  author: string;
  text: string;
  state: string;
  shortName: string;

  // FIXME: other solution?
  exercisesBasics: ExerciseBasics[];
}

export interface SemanticVersion {
  major: number;
  minor: number;
  patch: number;
}

export interface ExerciseBasics {
  id: number;
  collId: number;
  toolId: string;
  semanticVersion: SemanticVersion;
  title: string;
}

export interface Exercise extends ExerciseBasics {
  author: string;
  text: string;
  state: string;
  tags?: string[];
}

// Solutions

export interface DbSolution<T> {
  // toolId: string;
  collId: number;
  exId: number;
  // partId: string;
  solution: T;
}
