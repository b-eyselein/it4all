import {Component, Input, OnInit} from '@angular/core';
import {SimplifiedExecutionResultFragment} from "../../programming-apollo-mutations.service";
import {SuccessType} from "../../../../../_interfaces/graphql-types";

@Component({
  selector: 'it4all-programming-simplified-result',
  template: `
    <div class="message" [ngClass]="correct ? 'is-success' : 'is-danger'">
      <header class="message-header">{{result.id}}. Test war {{correct ? '' : 'nicht'}} erfolgreich.</header>
      <div class="message-body">

        <p>Eingabe: <code>{{result.input}}</code></p>
        <p>Erwartet: <code>{{result.awaited}}</code></p>

        <div *ngIf="isError">Fehlerausgabe:
          <pre>{{result.gotten}}</pre>
        </div>
        <p *ngIf="!isError">Bekommen: <code>{{result.gotten}}</code></p>
      </div>
    </div>`
})
export class ProgrammingSimplifiedResultComponent implements OnInit {

  @Input() result: SimplifiedExecutionResultFragment;

  correct = false;
  isError = false;

  ngOnInit(): void {
    this.correct = this.result.success === SuccessType.Complete;
    this.isError = this.result.success === SuccessType.Error;
  }

}
