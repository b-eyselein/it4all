import {Component, Input} from '@angular/core';
import {AnalysisResult, MatchingResult} from '../../../basics';

@Component({
  selector: 'it4all-sql-matching-result',
  templateUrl: './sql-matching-result.component.html'
})
export class SqlMatchingResultComponent {

  @Input() matchingResult: MatchingResult<string, AnalysisResult>;

}
