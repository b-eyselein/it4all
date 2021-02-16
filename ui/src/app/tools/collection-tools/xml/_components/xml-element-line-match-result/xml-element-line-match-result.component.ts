import {Component, Input, OnInit} from '@angular/core';
import {
  MatchType,
  XmlElementLineMatchFragment,
  XmlElementLineMatchingResultFragment
} from "../../../../../_services/apollo_services";

@Component({
  selector: 'it4all-xml-element-line-match-result',
  template: `
    <div *ngFor="let m of result.allMatches" class="content">

      <div [ngClass]="isCorrect(m) ? 'has-text-dark-success' : 'has-text-danger'">
        Die Definition des Element <code>{{m.userArg.elementName}}</code> ist {{isCorrect(m) ? '' : 'nicht' }}
        korrekt.

        <ul *ngIf="m.analysisResult && !isCorrect">
          <li [ngClass]="m.analysisResult.contentCorrect ? 'has-text-dark-success': 'has-text-danger'">
            Der Inhalt des Elements war {{m.analysisResult.contentCorrect ? '' : 'nicht'}} korrekt.

            <ng-container *ngIf="!m.analysisResult.contentCorrect">
              Erwartet wurde <code>{{m.analysisResult.correctContent}}</code>
            </ng-container>
          </li>
          <li [ngClass]="m.analysisResult.attributesCorrect ? 'has-text-dark-success' : 'has-text-danger'">
            Die Attribute des Elements waren {{m.analysisResult.attributesCorrect ? '' : 'nicht'}} korrekt.

            <ng-container *ngIf="!m.analysisResult.attributesCorrect">
              Erwartet wurde <code>{{m.analysisResult.correctAttributes}}</code>.
            </ng-container>
          </li>
        </ul>

      </div>

    </div>

    <div *ngFor="let mu of result.notMatchedForUser" class="has-text-danger">
      Die Definition des Elements <code>{{mu.elementName}}</code> ist falsch.
    </div>

    <div *ngFor="let ms of result.notMatchedForSample" class="has-text-danger">
      Die Definition des Elements <code>{{ms.elementName}}</code> fehlt.
    </div>
  `
})
export class XmlElementLineMatchResultComponent implements OnInit {

  @Input() result: XmlElementLineMatchingResultFragment;

  ngOnInit(): void {
  }

  isCorrect(m: XmlElementLineMatchFragment): boolean {
    return m.matchType === MatchType.SuccessfulMatch
  }

}
