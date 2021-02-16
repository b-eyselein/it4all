import {Component, Input} from '@angular/core';
import {UnitTestCorrectionResultFragment} from "../../../../../_services/apollo_services";

@Component({
  selector: 'it4all-programming-unit-test-result',
  template: `
    <li>
      <p [ngClass]="unitTestResult.successful ? 'has-text-success' : 'has-text-danger'">
        Der {{unitTestResult.testId}}. Test war {{unitTestResult.successful ? '' : 'nicht'}} erfolgreich.
      </p>
      <p *ngIf="!unitTestResult.successful">{{unitTestResult.description}}</p>
    </li>
  `,
})
export class ProgrammingUnitTestResultComponent {

  @Input() unitTestResult: UnitTestCorrectionResultFragment;

}
