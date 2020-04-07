import {Component, Input, OnInit} from '@angular/core';
import {SqlCellFragment, SqlQueryResultFragment, SqlRowFragment} from '../../sql-apollo-mutations.service';

@Component({
  selector: 'it4all-query-result-table',
  template: `
    <table class="table is-bordered is-fullwidth">
      <thead>
        <tr>
          <th *ngFor="let colName of queryResult.columnNames">{{colName}}</th>
        </tr>
      </thead>
      <tbody>
        <tr *ngFor="let row of queryResult.rows">
          <td *ngFor="let colName of queryResult.columnNames"
              [ngClass]="getCell(row, colName).different ? 'is-light-danger' : ''"
          >{{getCell(row, colName).content}}</td>
        </tr>
      </tbody>
    </table>`
})
export class QueryResultTableComponent implements OnInit {

  @Input() queryResult: SqlQueryResultFragment;

  constructor() {
  }

  ngOnInit(): void {
  }

  getCell(row: SqlRowFragment, key: String): SqlCellFragment | undefined {
    return row.cells.find((r) => r.key === key).value;
  }

}
