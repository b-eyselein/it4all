import {Component, Input, OnInit} from '@angular/core';
import {RegexMatchingSingleResultFragment} from '../regex-apollo-mutations.service';

@Component({
  selector: 'it4all-regex-matching-result',
  template: `
    <div class="notification is-light-grey" [ngClass]="correct ? 'has-text-dark-success' : 'has-text-danger'">
      {{correct ? '&#10004;' : '&#10008;'}} &nbsp;
      <code>{{matchingResult.matchData}}</code> wurde {{correct ? 'korrekt' : 'fälschlicherweise'}}
      <span [innerHTML]="wasMatched ? '' : '<b>nicht</b>'"></span> erkannt.
    </div>`
})
export class RegexMatchingResultComponent implements OnInit {

  @Input() matchingResult: RegexMatchingSingleResultFragment;

  correct: boolean;
  wasMatched: boolean;

  ngOnInit(): void {
    this.correct = ['TruePositive', 'TrueNegative'].includes(this.matchingResult.resultType);
    this.wasMatched = ['FalsePositive' || 'TruePositive'].includes(this.matchingResult.resultType);
  }

}
