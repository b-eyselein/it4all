import {Component, Input} from '@angular/core';
import {FieldsForLinkFragment} from '../../../../_services/apollo_services';

@Component({
  selector: 'it4all-exercise-link-card',
  templateUrl: './exercise-link-card.component.html'
})
export class ExerciseLinkCardComponent {

  @Input() exercise: FieldsForLinkFragment;

}
