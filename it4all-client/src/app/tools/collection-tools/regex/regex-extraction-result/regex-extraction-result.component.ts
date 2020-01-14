import {Component, Input} from '@angular/core';
import {IRegexExtractionEvaluationResult} from '../regex-interfaces';

@Component({
  selector: 'it4all-regex-extraction-result',
  templateUrl: './regex-extraction-result.component.html',
})
export class RegexExtractionResultComponent {

  @Input() extractionResult: IRegexExtractionEvaluationResult;

  constructor() {
  }

  isCompletelyCorrect(): boolean {
    return this.extractionResult.extractionMatchingResult.allMatches
      .every((m) => m.analysisResult.matchType === 'SUCCESSFUL_MATCH');
  }

}
