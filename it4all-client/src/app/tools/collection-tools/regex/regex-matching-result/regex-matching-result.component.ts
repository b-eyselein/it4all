import {Component, Input} from '@angular/core';
import {IRegexMatchingEvaluationResult} from '../regex-interfaces';

@Component({
  selector: 'it4all-regex-matching-result',
  templateUrl: './regex-matching-result.component.html',
})
export class RegexMatchingResultComponent {

  @Input() matchingResult: IRegexMatchingEvaluationResult;

  constructor() {
  }

  isCorrect(): boolean {
    return this.matchingResult.resultType === 'TruePositive' || this.matchingResult.resultType === 'TrueNegative';
  }

}
