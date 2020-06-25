import {Component, Input} from '@angular/core';
import {ExerciseFileFragment} from '../../../../_services/apollo_services';

@Component({
  selector: 'it4all-exercise-file-card',
  template: `
    <div class="card">
      <header class="card-header">
        <p class="card-header-title">{{exerciseFile.name}}</p>
      </header>
      <div class="card-content">
        <pre>{{exerciseFile.content}}</pre>
      </div>
    </div>
  `
})
export class ExerciseFileCardComponent {

  @Input() exerciseFile: ExerciseFileFragment;

}
