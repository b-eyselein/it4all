import {Component, Input, OnInit} from '@angular/core';
import {NormalExecutionResultFragment} from "../../programming-apollo-mutations.service";
import {SuccessType} from "../../../../../_interfaces/graphql-types";

@Component({
  selector: 'it4all-programming-normal-result',
  template: `
    <div class="notification" [ngClass]="isSuccess ? 'is-success': 'is-danger'">
      <pre>{{normalResult.logs}}</pre>
    </div>
  `
})
export class ProgrammingNormalResultComponent implements OnInit {

  @Input() normalResult: NormalExecutionResultFragment;

  isSuccess = false;

  ngOnInit(): void {
    this.isSuccess = this.normalResult.success === SuccessType.Complete;
  }

}
