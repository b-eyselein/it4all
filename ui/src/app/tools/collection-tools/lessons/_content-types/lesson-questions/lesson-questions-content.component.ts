import {Component, Input, OnInit} from '@angular/core';
import {LessonAnswer, LessonQuestion, LessonQuestionsContent} from '../../../../../_interfaces/lesson';

@Component({
  selector: 'it4all-lesson-questions-content',
  templateUrl: './lesson-questions-content.component.html'
})
export class LessonQuestionsContentComponent implements OnInit {

  @Input() content: LessonQuestionsContent;

  ngOnInit() {
  }

  correct(question: LessonQuestion, answer: LessonAnswer): void {
    console.info(question);
    console.info(answer);
  }

}
