import {Component, Input} from '@angular/core';
import {
  LessonMultipleChoiceQuestionAnswerFragment,
  LessonMultipleChoiceQuestionContentFragment,
  LessonMultipleChoiceQuestionFragment
} from '../../../../../_services/apollo_services';

@Component({
  selector: 'it4all-lesson-questions-content',
  template: `
    <div *ngFor="let question of content.questions">
      <p [innerHTML]="question.question"></p>
      <div class="columns">
        <div class="column" *ngFor="let answer of question.answers">
          <button class="button is-fullwidth is-info" (click)="correct(question, answer)" [innerHTML]="answer.answer"></button>
        </div>
      </div>
    </div>
  `
})
export class LessonQuestionsContentComponent {

  @Input() content: LessonMultipleChoiceQuestionContentFragment;

  correct(question: LessonMultipleChoiceQuestionFragment, answer: LessonMultipleChoiceQuestionAnswerFragment): void {
    console.info(question);
    console.info(answer);
  }

}
