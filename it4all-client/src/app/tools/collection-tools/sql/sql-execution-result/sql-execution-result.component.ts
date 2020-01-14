import {Component, Input} from '@angular/core';
import {ISqlExecutionResult} from '../sql-interfaces';

@Component({
  selector: 'it4all-sql-execution-result',
  templateUrl: './sql-execution-result.component.html'
})
export class SqlExecutionResultComponent {

  @Input() result: ISqlExecutionResult;

}
