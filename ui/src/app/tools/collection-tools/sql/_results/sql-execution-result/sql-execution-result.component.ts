import {Component, Input} from '@angular/core';
import {SqlExecutionResultFragment} from "../../sql-apollo-mutations.service";

@Component({
  selector: 'it4all-sql-execution-result',
  template: `
    <h3 class="subtitle is-4 has-text-centered">Vergleich der Ergebnistabellen</h3>

    <div class="columns">
      <div class="column">
        <h4 class="subtitle is-4 has-text-centered">Nutzer</h4>
        <div class="table-container" *ngIf="result.userResultTry; else noUserResultBlock">
          <it4all-query-result-table [queryResult]="result.userResultTry"></it4all-query-result-table>
        </div>
      </div>

      <div class="column">
        <h4 class="subtitle is-4 has-text-centered">Muster</h4>
        <div class="table-container">
          <it4all-query-result-table [queryResult]="result.sampleResultTry"></it4all-query-result-table>
        </div>
      </div>
    </div>

    <ng-template #noUserResultBlock>
      <div class="notification is-danger">Es gab einen Fehler beim Ausführen ihrer Query!</div>
    </ng-template>`
})
export class SqlExecutionResultComponent {

  @Input() result: SqlExecutionResultFragment;

}
