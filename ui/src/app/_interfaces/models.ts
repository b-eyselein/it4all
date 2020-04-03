interface IExTag {
  abbreviation: string;
  title: string;
}


export interface IExerciseMetaData {
  id: number;
  collectionId: number;
  toolId: string;
  semanticVersion: ISemanticVersion;
  title: string;
  authors: string[];
  text: string;
  tags: IExTag[];
  difficulty?: number;
}


export interface IExerciseCollection {
  id: number;
  toolId: string;
  title: string;
  authors: string[];
  text: string;
  shortName: string;
}


export interface ISemanticVersion {
  major: number;
  minor: number;
  patch: number;
}


export interface IExercise extends IExerciseMetaData {
  content: any;
}
