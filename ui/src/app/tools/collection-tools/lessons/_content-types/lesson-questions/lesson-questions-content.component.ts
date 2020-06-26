import {Component, Input} from '@angular/core';
import {
  LessonMultipleChoiceQuestionAnswerFragment,
  LessonMultipleChoiceQuestionContentFragment,
  LessonMultipleChoiceQuestionFragment
} from '../../../../../_services/apollo_services';

@Component({
  selector: 'it4all-lesson-questions-content',
  templateUrl: './lesson-questions-content.component.html'
})
export class LessonQuestionsContentComponent {

  @Input() content: LessonMultipleChoiceQuestionContentFragment;

  correct(question: LessonMultipleChoiceQuestionFragment, answer: LessonMultipleChoiceQuestionAnswerFragment): void {
    console.info(question);
    console.info(answer);
  }

}
