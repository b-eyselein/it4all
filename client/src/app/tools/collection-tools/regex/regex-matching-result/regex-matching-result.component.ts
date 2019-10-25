import {Component, Input} from '@angular/core';
import {RegexMatchingResult} from '../regex-exercise';

@Component({
  selector: 'it4all-regex-matching-result',
  template: `
      <div class="notification" [class.is-success]="isCorrect()" [class.is-danger]="!isCorrect()">
          <span *ngIf="isCorrect()">&#10004;</span>
          <span *ngIf="!isCorrect()">&#10008;</span>
          &nbsp;
          <code>{{matchingResult.matchData}}</code> wurde
          <ng-container [ngSwitch]="matchingResult.resultType">
              <span *ngSwitchCase="'TruePositive'">korrekt</span>
              <span *ngSwitchCase="'FalsePositive'">fälschlicherweise</span>
              <span *ngSwitchCase="'FalseNegative'">fälschlicherweise <b>nicht</b></span>
              <span *ngSwitchCase="'TrueNegative'">korrekt <b>nicht</b></span>
          </ng-container>
          erkannt.
      </div>`,
})
export class RegexMatchingResultComponent {

  @Input() matchingResult: RegexMatchingResult;

  constructor() {
  }

  isCorrect(): boolean {
    return this.matchingResult.resultType === 'TruePositive' || this.matchingResult.resultType === 'TrueNegative';
  }

}
