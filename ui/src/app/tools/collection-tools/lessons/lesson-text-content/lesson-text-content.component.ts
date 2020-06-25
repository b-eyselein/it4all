import {Component, Input, OnInit} from '@angular/core';
import {LessonTextContent} from '../../../../_interfaces/lesson';

@Component({
  selector: 'it4all-lesson-text-content',
  template: '<div class="content box" [innerHTML]="content.content"></div>'
})
export class LessonTextContentComponent implements OnInit {

  @Input() content: LessonTextContent;

  constructor() {
  }

  ngOnInit() {
  }

}
