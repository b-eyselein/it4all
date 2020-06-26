import {Component, Input} from '@angular/core';
import {LessonTextContentFragment} from '../../../../_services/apollo_services';

@Component({
  selector: 'it4all-lesson-text-content',
  template: '<div class="content box" [innerHTML]="content.content"></div>'
})
export class LessonTextContentComponent {

  @Input() content: LessonTextContentFragment;

}
