import {Component, Input} from '@angular/core';
import {ISqlResult} from '../sql-interfaces';

@Component({
  selector: 'it4all-sql-result',
  templateUrl: './sql-result.component.html'
})
export class SqlResultComponent {

  @Input() result: ISqlResult;

}
