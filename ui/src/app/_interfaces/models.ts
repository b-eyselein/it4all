
export interface IQuestion {
  id: number;
  question: string;
  answers: IQuestionAnswer[];
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


export interface IQuestionAnswer {
  answer: string;
  isCorrect: boolean;
}


export interface ILessonTextContent {
  id: number;
  lessonId: number;
  toolId: string;
  content: string;
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


export interface ILesson {
  id: number;
  toolId: string;
  title: string;
  content: LessonContent[];
}


export type LessonContent = (ILessonQuestionsContent | ILessonTextContent);

export interface IExTag {
  abbreviation: string;
  title: string;
}


export interface IExerciseCollection {
  id: number;
  toolId: string;
  title: string;
  authors: string[];
  text: string;
  shortName: string;
}


export interface ILessonQuestionsContent {
  id: number;
  lessonId: number;
  toolId: string;
  questions: IQuestion[];
}
