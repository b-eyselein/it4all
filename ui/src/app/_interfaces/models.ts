/**
 * @deprecated
 */
export interface IExTag {
  abbreviation: string;
  title: string;
}

/**
 * @deprecated
 */
export interface ISemanticVersion {
  major: number;
  minor: number;
  patch: number;
}

/**
 * @deprecated
 */
export interface IExercise {
  id: number;
  collectionId: number;
  toolId: string;
  semanticVersion: ISemanticVersion;
  title: string;
  authors: string[];
  text: string;
  tags: IExTag[];
  difficulty?: number;
  content: any;
}

/**
 * @deprecated
 */
export interface IExerciseCollection {
  id: number;
  toolId: string;
  title: string;
  authors: string[];
  text: string;
  shortName: string;
}
