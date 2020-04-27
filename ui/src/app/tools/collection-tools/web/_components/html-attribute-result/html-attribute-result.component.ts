import {Component, Input} from '@angular/core';
import {GradedTextContentResultFragment} from "../../web-apollo-mutations.service";

@Component({
  selector: 'it4all-html-attribute-result',
  template: `
    <li [ngClass]="attributeResult.isSuccessful ? 'has-text-success' : 'has-text-danger'">
      Das Attribut <code>{{attributeResult.keyName}}</code>
      <ng-container *ngIf="attributeResult.maybeFoundContent">
        sollte den Wert <code>{{attributeResult.awaitedContent}}</code> haben.
        Gefunden wurde <code>{{attributeResult.maybeFoundContent}}</code>.
      </ng-container>
      <ng-container *ngIf="!attributeResult.maybeFoundContent">
        wurde nicht gefunden!
      </ng-container>
    </li>
  `
})
export class HtmlAttributeResultComponent {

  @Input() attributeResult: GradedTextContentResultFragment;

}
