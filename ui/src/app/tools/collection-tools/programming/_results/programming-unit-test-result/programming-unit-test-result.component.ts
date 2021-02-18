import {Component, Input} from '@angular/core';
import {UnitTestCorrectionResultFragment} from "../../../../../_services/apollo_services";

@Component({
  selector: 'it4all-programming-unit-test-result',
  template: `
    <li>
      <p [ngClass]="unitTestResult.successful ? 'has-text-success' : 'has-text-danger'">
        Der {{unitTestResult.testId}}. Test war {{unitTestResult.successful ? '' : 'nicht'}} erfolgreich.
        Der Test sollte {{unitTestResult.shouldFail ? '' : 'nicht'}} fehlschlagen.
      </p>
      <ng-container *ngIf="!unitTestResult.successful">
        <p>Beschreibung: {{unitTestResult.description}}</p>
        <pre>{{stderr}}</pre>
      </ng-container>
    </li>
  `,
})
export class ProgrammingUnitTestResultComponent {

  @Input() unitTestResult: UnitTestCorrectionResultFragment;

  get stderr() {
    return this.unitTestResult.stderr.join('\n');
  }

}
