import {Component, Input} from '@angular/core';
import {SqlExecutionResultFragment} from "../../sql-apollo-mutations.service";

@Component({
  selector: 'it4all-sql-execution-result',
  templateUrl: './sql-execution-result.component.html'
})
export class SqlExecutionResultComponent {

  @Input() result: SqlExecutionResultFragment;

}
