import {Component, Input, OnInit, OnChanges, SimpleChanges} from '@angular/core';
import {RegexMatchingResult} from '../regex';

@Component({
  selector: 'it4all-regex-matching-result',
  template: `
      <div class="notification">
          <code>{{matchingResult.matchData}}</code> wurde
          <ng-container [ngSwitch]="matchingResult.resultType">
              <span class="has-text-success" *ngSwitchCase="'TruePositive'">korrekt</span>
              <span class="has-text-danger" *ngSwitchCase="'FalsePositive'">fälschlicherweise</span>
              <span class="has-text-danger" *ngSwitchCase="'FalseNegative'">fälschlicherweise nicht</span>
              <span class="has-text-success" *ngSwitchCase="'TrueNegative'">korrekt nicht</span>
          </ng-container>
          erkannt.
      </div>`,
})
export class RegexMatchingResultComponent implements OnInit, OnChanges {

  @Input() matchingResult: RegexMatchingResult;

  constructor() {
  }

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges): void {
  }

}
