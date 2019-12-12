type TutorialContentType = 'Text' | 'TrueFalseQuestion' | 'Definition';

export interface LessonContentBase {
  _type: TutorialContentType;
  priorSolved?: boolean;
}

export interface LessonTextContent extends LessonContentBase {
  _type: 'Text';
  content: string;
}

export interface LessonTrueFalseQuestion extends LessonContentBase {
  _type: 'TrueFalseQuestion';
  question: string;
  isTrue: boolean;
}

type LessonContent = LessonTextContent | LessonTrueFalseQuestion;

export interface Lesson {
  id: number;
  title: string;
  description: string;
  content: LessonContent[];
  dependsOn?: number[];
}
