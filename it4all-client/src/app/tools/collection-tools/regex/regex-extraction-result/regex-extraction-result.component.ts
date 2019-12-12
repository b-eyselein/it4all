import {Component, Input} from '@angular/core';
import {RegexExtractionResult} from '../regex-interfaces';

@Component({
  selector: 'it4all-regex-extraction-result',
  templateUrl: './regex-extraction-result.component.html',
})
export class RegexExtractionResultComponent {

  @Input() extractionResult: RegexExtractionResult;

  constructor() {
  }

  isCompletelyCorrect(): boolean {
    return this.extractionResult.extractionMatchingResult.allMatches.every((m) => m.matchType === 'SUCCESSFUL_MATCH');
  }

}
