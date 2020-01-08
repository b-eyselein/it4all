import {Component, Input} from '@angular/core';
import {SqlResult} from '../sql-interfaces';

@Component({
  selector: 'it4all-sql-result',
  templateUrl: './sql-result.component.html'
})
export class SqlResultComponent {

  @Input() result: SqlResult;

}
