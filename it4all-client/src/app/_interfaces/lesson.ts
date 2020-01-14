type TutorialContentType = 'Text' | 'TrueFalseQuestion' | 'Questions' | 'Definition';

export interface LessonContentBase {
  _type: TutorialContentType;
  id: number;
  lessonId: number;
  toolId: string;
  priorSolved?: boolean;
}

export interface LessonTextContent extends LessonContentBase {
  _type: 'Text';
  content: string;
}

export interface LessonAnswer {
  answer: string;
  isCorrect: boolean;
}

export interface LessonQuestion {
  id: number;
  question: string;
  answers: LessonAnswer[];
}

export interface LessonQuestionsContent extends LessonContentBase {
  _type: 'Questions';
  questions: LessonQuestion[];
}

export interface LessonTrueFalseQuestion extends LessonContentBase {
  _type: 'TrueFalseQuestion';
  question: string;
  isTrue: boolean;
}

export function isLessonTextContent(content: LessonContentBase): content is LessonTextContent {
  return content._type === 'Text';
}

type LessonContent = LessonTextContent | LessonQuestionsContent | LessonTrueFalseQuestion;

/**
 * @deprecated
 */
export interface Lesson {
  id: number;
  toolId: string;
  title: string;
  description: string;
  content: LessonContent[];
  dependsOn?: number[];
}
