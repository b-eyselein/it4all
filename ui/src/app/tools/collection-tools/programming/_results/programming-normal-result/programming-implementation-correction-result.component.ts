import {Component, Input} from '@angular/core';
import {ImplementationCorrectionResult} from "../../../../../_services/apollo_services";

@Component({
  selector: 'it4all-programming-normal-result',
  template: `
    <div class="notification" [ngClass]="normalResult.successful ? 'is-success': 'is-danger'">
      <pre>{{stderr}}</pre>
    </div>
  `
})
export class ProgrammingImplementationCorrectionResultComponent {

  @Input() normalResult: ImplementationCorrectionResult;

  get stderr(): string {
    return this.normalResult.stderr.join('\n');
  }

}