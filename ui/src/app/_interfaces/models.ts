
export interface IExTag {
  abbreviation: string;
  title: string;
}


export interface ISemanticVersion {
  major: number;
  minor: number;
  patch: number;
}


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


export interface IExerciseCollection {
  id: number;
  toolId: string;
  title: string;
  authors: string[];
  text: string;
  shortName: string;
}
