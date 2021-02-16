import {Component, Input} from '@angular/core';
import {SqlQueryResultFragment} from "../../../../../_services/apollo_services";

@Component({
  selector: 'it4all-sql-table-contents',
  template: `
    <div class="columns is-multiline">
      <div class="column is-one-quarter-desktop" *ngFor="let dbContent of dbContents">
        <button class="button is-fullwidth" (click)="activateModal(dbContent)"
                [class.is-info]="dbContent === shownDbContent">
          {{dbContent.tableName}}
        </button>
      </div>
    </div>

    <div class="card" *ngIf="shownDbContent">
      <header class="card-header">
        <p class="card-header-title">{{shownDbContent.tableName}}</p>
      </header>
      <section class="card-content">
        <div class="table-container">
          <it4all-query-result-table [queryResult]="shownDbContent"></it4all-query-result-table>
        </div>
      </section>
    </div>`
})
export class SqlTableContentsComponent {

  @Input() dbContents: SqlQueryResultFragment[];

  shownDbContent: SqlQueryResultFragment | undefined;

  activateModal(sqlQueryResult: SqlQueryResultFragment): void {
    if (this.shownDbContent === sqlQueryResult) {
      this.shownDbContent = undefined;
    } else {
      this.shownDbContent = sqlQueryResult;
    }
  }

}
