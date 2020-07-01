import {Component, Input, OnInit} from '@angular/core';
import {
  LessonMultipleChoiceQuestionAnswerFragment,
  LessonMultipleChoiceQuestionContentFragment,
  LessonMultipleChoiceQuestionFragment
} from '../../../../../_services/apollo_services';

interface SelectableAnswer extends LessonMultipleChoiceQuestionAnswerFragment {
  selected: boolean;
}

interface QuestionWithSelectableAnswer {
  question: LessonMultipleChoiceQuestionFragment;
  corrected: boolean;
  answers: SelectableAnswer[];
}

function shuffleArray<T>(array: T[]): void {
  for (let i = array.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [array[i], array[j]] = [array[j], array[i]];
  }
}

@Component({
  selector: 'it4all-lesson-questions-content',
  template: `
    <div class="card my-3" *ngFor="let questionWithSelectableAnswer of selectableAnswers">
      <header class="card-header">
        <p class="card-header-title" [innerHTML]="questionWithSelectableAnswer.question.questionText">
          {{ questionWithSelectableAnswer.question.questionText }}
        </p>
      </header>

      <div class="card-content">
        <div class="field" *ngFor="let answer of questionWithSelectableAnswer.answers">
          <label class="checkbox" [ngClass]="isCorrectedAndCorrect(questionWithSelectableAnswer, answer)">
            <input type="checkbox" (change)="answer.selected = !answer.selected">
            &nbsp;
            <span [innerHTML]="answer.answer">{{ answer.answer }}</span>
          </label>
        </div>

        <button class="button is-link"
                (click)="questionWithSelectableAnswer.corrected = true"
                [disabled]="questionWithSelectableAnswer.corrected">
          Korrektur
        </button>

      </div>
    </div>
  `
})
export class LessonQuestionsContentComponent implements OnInit {

  @Input() private content: LessonMultipleChoiceQuestionContentFragment;

  selectableAnswers: QuestionWithSelectableAnswer[];

  ngOnInit() {
    this.selectableAnswers = this.content.questions.map((question) => {
      const answers: SelectableAnswer[] = question.answers.map((answer) => {
        return {selected: false, ...answer};
      });

      shuffleArray(answers);

      return {question, corrected: false, answers};
    });
  }

  isCorrectedAndCorrect(questionWithSelectableAnswer: QuestionWithSelectableAnswer, answer: SelectableAnswer): string {
    if (questionWithSelectableAnswer.corrected) {
      return 'TODO!';
    } else {
      return '';
    }
  }

}
