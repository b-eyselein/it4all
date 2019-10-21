export type ToolStatus = 'live' | 'alpha' | 'beta';

export interface ToolPart {
  id: string;
  name: string;
  disabled?: boolean;
}

export interface Tool {
  id: string;
  name: string;
  // FIXME: empty if only one path ?!?
  parts: ToolPart[];
  status?: ToolStatus;
  hasPlayground?: boolean;
  hasLivePreview?: boolean;
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
