import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {
  MatchType,
  UmlImplementationMatchFragment,
  UmlImplementationMatchingResultFragment
} from "../../../../../_services/apollo_services";

@Component({
  selector: 'it4all-uml-impl-result',
  template: `
    <h3 class="subtitle is-5" [ngClass]="implResultSuccessful ? 'has-text-dark-success' : 'has-text-danger'">
      Der Vergleich der Vererbungen war {{implResultSuccessful ? '' : 'nicht'}} erfolgreich.
    </h3>

    <div class="content" *ngIf="!implResultSuccessful">
      <ul>
        <li *ngFor="let m of implResult.allMatches">
          <div [ngClass]="isCorrect(m) ? 'has-text-dark-success' : 'has-text-danger'">
            Die Vererbung von <code>{{m.userArg.subClass}}</code> nach
            <code>{{m.userArg.superClass}}</code> ist {{isCorrect(m) ? '' : 'nicht'}} korrekt.

            <div *ngIf="!isCorrect(m)" class="content">
              <ul>
                <li [ngClass]="directionCorrect(m) ? 'has-text-dark-success' : 'has-text-danger'">
                  Die Vererbungsrichtung war {{directionCorrect(m) ? '' : 'nicht'}} korrekt.
                </li>
              </ul>
            </div>
          </div>
        </li>

        <li *ngFor="let mu of implResult.notMatchedForUser" class="has-text-danger">
          Die Vererbung von <code>{{mu.subClass}}</code> nach <code>{{mu.superClass}}</code> ist falsch.
        </li>

        <li *ngFor="let ms of implResult.notMatchedForSample" class="has-text-danger">
          Die Vererbung von <code>{{ms.subClass}}</code> nach <code>{{ms.superClass}}</code> fehlt.
        </li>
      </ul>
    </div>
  `
})
export class UmlImplResultComponent implements OnChanges {

  @Input() implResult: UmlImplementationMatchingResultFragment;

  implResultSuccessful = false;

  ngOnChanges(changes: SimpleChanges) {
    this.implResultSuccessful = this.implResult.allMatches.every(this.isCorrect);
  }

  isCorrect(m: UmlImplementationMatchFragment): boolean {
    return m.matchType === MatchType.SuccessfulMatch;
  }

  directionCorrect(m: UmlImplementationMatchFragment): boolean {
    return m.userArg.subClass === m.sampleArg.subClass;
  }


}
