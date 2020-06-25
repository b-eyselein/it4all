import {Component, Input} from '@angular/core';
import {UnitTestCorrectionResultFragment} from '../../programming-apollo-mutations.service';

@Component({
  selector: 'it4all-programming-unit-test-result',
  template: `
    <li>
      <p [ngClass]="unitTestResult.successful ? 'has-text-success' : 'has-text-danger'">
        Der {{unitTestResult.testConfig.id}}. Test war
        <ng-container *ngIf="!unitTestResult.successful">nicht</ng-container>
        erfolgreich.
      </p>
      <p *ngIf="!unitTestResult.successful">{{unitTestResult.testConfig.description}}</p>
    </li>
  `,
})
export class ProgrammingUnitTestResultComponent {

  @Input() unitTestResult: UnitTestCorrectionResultFragment;

}
