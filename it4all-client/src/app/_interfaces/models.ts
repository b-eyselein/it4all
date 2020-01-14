
export interface IExercise {
  id: number;
  collectionId: number;
  toolId: string;
  semanticVersion: ISemanticVersion;
  title: string;
  authors: string[];
  text: string;
  tags: IExTag[];
  content: any;
}


export interface IExTag {
  abbreviation: string;
  title: string;
}


export interface IQuestion {
  id: number;
  question: string;
  answers: IQuestionAnswer[];
}


export interface ILessonQuestionsContent {
  id: number;
  lessonId: number;
  toolId: string;
  questions: IQuestion[];
}


export interface IExerciseCollection {
  id: number;
  toolId: string;
  title: string;
  authors: string[];
  text: string;
  shortName: string;
}


export interface ILessonTextContent {
  id: number;
  lessonId: number;
  toolId: string;
  content: string;
}


export interface ILesson {
  id: number;
  toolId: string;
  title: string;
  content: LessonContent[];
}


export interface ISemanticVersion {
  major: number;
  minor: number;
  patch: number;
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
}


export interface IQuestionAnswer {
  answer: string;
  isCorrect: boolean;
}



export type LessonContent = (ILessonTextContent | ILessonQuestionsContent);