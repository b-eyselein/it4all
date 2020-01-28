import {Component, Input} from '@angular/core';
import {IExerciseMetaData} from '../../../../_interfaces/models';

@Component({
  selector: 'it4all-exercise-link-card',
  templateUrl: './exercise-link-card.component.html'
})
export class ExerciseLinkCardComponent {

  readonly Arr = Array;

  @Input() exercise: IExerciseMetaData;

}
