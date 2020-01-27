import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {IMatchingResult} from '../../sql-interfaces';

@Component({
  selector: 'it4all-sql-matching-result',
  templateUrl: './sql-matching-result.component.html'
})
export class SqlMatchingResultComponent implements OnChanges {

  @Input() matchingResult: IMatchingResult;

  successful: boolean;

  ngOnChanges(changes: SimpleChanges): void {
    this.successful = this.matchingResult.allMatches.every(
      (m) => m.analysisResult.matchType === 'SUCCESSFUL_MATCH'
    );
  }

}
