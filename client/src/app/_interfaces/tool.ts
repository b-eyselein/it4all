export type ToolStatus = 'live' | 'alpha' | 'beta';

export interface ToolPart {
  id: string;
  name: string;
  disabled?: boolean;
}

export class Tool {
  constructor(
    public id: string,
    public name: string,
    // FIXME: empty if only one part ?!?
    public parts?: ToolPart[],
    public status?: ToolStatus,
    public hasPlayground?: boolean,
    public hasLivePreview?: boolean
  ) {
  }

  exerciseHasPart(exercise: Exercise, part: ToolPart): boolean {
    return true;
  }

}

export interface ExerciseCollection {
  id: number;
  toolId: string;
  title: string;
  author: string;
  text: string;
  state: string;
  shortName: string;
}

export interface SemanticVersion {
  major: number;
  minor: number;
  patch: number;
}

export interface ExerciseBasics {
  id: number;
  semanticVersion: SemanticVersion;
  title: string;
}

export interface Exercise extends ExerciseBasics {
  author: string;
  text: string;
  state: string;
}
