import {Component, Input} from '@angular/core';
import {ISqlQueryResult} from '../../sql-interfaces';

@Component({
  selector: 'it4all-sql-table-contents',
  templateUrl: './sql-table-contents.component.html'
})
export class SqlTableContentsComponent {

  @Input() dbContents: ISqlQueryResult[];

  shownDbContent: ISqlQueryResult | undefined;

  activateModal(sqlQueryResult: ISqlQueryResult): void {
    if (this.shownDbContent === sqlQueryResult) {
      this.shownDbContent = undefined;
    } else {
      this.shownDbContent = sqlQueryResult;
    }
  }

}
