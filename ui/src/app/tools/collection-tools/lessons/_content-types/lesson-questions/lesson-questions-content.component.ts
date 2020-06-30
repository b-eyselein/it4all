import {Component, Input, OnInit} from '@angular/core';
import {
  LessonMultipleChoiceQuestionAnswerFragment,
  LessonMultipleChoiceQuestionContentFragment,
  LessonMultipleChoiceQuestionFragment
} from '../../../../../_services/apollo_services';

@Component({
  selector: 'it4all-lesson-questions-content',
  template: `
    <div class="card my-3" *ngFor="let question of content.questions">
      <header class="card-header">
        <p class="card-header-title" [innerHTML]="question.question">{{ question.question }}</p>
      </header>

      <div class="card-content">
        <div class="field" *ngFor="let answer of question.answers">
          <label class="checkbox">
            <input type="checkbox" (change)="toggleAnswer(question, answer)">&nbsp;<span
            [innerHTML]="answer.answer">{{ answer.answer }}</span>
          </label>
        </div>

        <button class="button is-link" (click)="correct(question)">Korrektur</button>

      </div>
    </div>
  `
})
export class LessonQuestionsContentComponent implements OnInit {

  @Input() content: LessonMultipleChoiceQuestionContentFragment;

  selectedAnswers: Map<number, number[]>;

  ngOnInit() {
    this.selectedAnswers = new Map(this.content.questions.map((question) => [question.id, []]));
  }

  toggleAnswer(question: LessonMultipleChoiceQuestionFragment, answer: LessonMultipleChoiceQuestionAnswerFragment): void {
    const oldAnswers: number[] = this.selectedAnswers.get(question.id) || [];

    const newAnswers: number[] = oldAnswers.includes(answer.id) ?
      oldAnswers.filter((a) => a !== answer.id) :
      [...oldAnswers, answer.id];

    this.selectedAnswers.set(question.id, newAnswers);
  }

  correct(question: LessonMultipleChoiceQuestionFragment): void {
    const correctAnswers = question.answers
      .filter((answer) => answer.isCorrect)
      .map((answer) => answer.id);

    const selectedAnswers = this.selectedAnswers.get(question.id);

    console.info('Correct:  ' + correctAnswers);
    console.info('Selected: ' + selectedAnswers);
  }

}
