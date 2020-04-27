import {Component, Input} from '@angular/core';
import {GradedHtmlTaskResultFragment} from "../../web-apollo-mutations.service";

@Component({
  selector: 'it4all-html-task-result',
  template: `
    <span [ngClass]="htmlResult.isSuccessful ? 'has-text-success' : 'has-text-danger'">
      ({{htmlResult.points}} / {{htmlResult.maxPoints}})
      Teilaufgabe {{htmlResult.id}} ist {{htmlResult.isSuccessful ? '' : 'nicht'}} korrekt:
    </span>

    <ul>
      <li [ngClass]="htmlResult.elementFound ? 'has-text-success' : 'has-text-danger'">
        Das Element konnte {{htmlResult.elementFound ? '' : 'nicht'}} gefunden werden!
      </li>

      <ng-container *ngFor="let attributeResult of htmlResult.attributeResults">
        <it4all-html-attribute-result [attributeResult]="attributeResult"></it4all-html-attribute-result>
      </ng-container>

      <ng-container *ngIf="htmlResult.textContentResult">
        <li [ngClass]="htmlResult.textContentResult.isSuccessful ? 'has-text-success' : 'has-text-danger'">
          Das Element sollte den Textinhalt <code>{{htmlResult.textContentResult.awaitedContent}}</code> haben.
          Gefunden wurde <code>{{htmlResult.textContentResult.maybeFoundContent}}</code>.
        </li>
      </ng-container>
    </ul>
  `
})
export class HtmlTaskResultComponent {

  @Input() htmlResult: GradedHtmlTaskResultFragment;

}
