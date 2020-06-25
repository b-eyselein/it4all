type TutorialContentType = 'Text' | 'TrueFalseQuestion' | 'Questions' | 'Definition';

/**
 * @deprecated
 */
export interface LessonContentBase {
  _type: TutorialContentType;
  id: number;
  lessonId: number;
  toolId: string;
  priorSolved?: boolean;
}

/**
 * @deprecated
 */
export interface LessonTextContent extends LessonContentBase {
  _type: 'Text';
  content: string;
}

/**
 * @deprecated
 */
export interface LessonAnswer {
  answer: string;
  isCorrect: boolean;
}

/**
 * @deprecated
 */
export interface LessonQuestion {
  id: number;
  question: string;
  answers: LessonAnswer[];
}

/**
 * @deprecated
 */
export interface LessonQuestionsContent extends LessonContentBase {
  _type: 'Questions';
  questions: LessonQuestion[];
}
