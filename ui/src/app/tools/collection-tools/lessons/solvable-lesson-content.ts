import {LessonMultipleChoiceQuestionContentFragment, LessonTextContentFragment} from '../../../_services/apollo_services';

export type LessonContentFragmentTypes = LessonTextContentFragment | LessonMultipleChoiceQuestionContentFragment;

export function isSolvableLessonTextContentFragment(
  lessonContentFragment: LessonContentFragmentTypes
): lessonContentFragment is LessonTextContentFragment {
  return lessonContentFragment.__typename === 'LessonTextContent';
}

export function isSolvableLessonMultipleChoiceQuestionContentFragment(
  lessonContentFragment: LessonContentFragmentTypes
): lessonContentFragment is LessonMultipleChoiceQuestionContentFragment {
  return lessonContentFragment.__typename === 'LessonMultipleChoiceQuestionsContent';
}

