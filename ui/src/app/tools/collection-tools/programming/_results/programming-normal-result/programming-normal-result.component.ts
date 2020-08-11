import {Component, Input} from '@angular/core';
import {NormalExecutionResultFragment} from '../../programming-apollo-mutations.service';

@Component({
  selector: 'it4all-programming-normal-result',
  template: `
    <div class="notification" [ngClass]="normalResult.successful ? 'is-success': 'is-danger'">
      <pre>{{normalResult.logs}}</pre>
    </div>
  `
})
export class ProgrammingNormalResultComponent {

  @Input() normalResult: NormalExecutionResultFragment;

}
