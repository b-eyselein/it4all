import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {MatchingResult} from '../../../../../_services/apollo-mutation.service';

@Component({
  selector: 'it4all-sql-matching-result',
  templateUrl: './sql-matching-result.component.html'
})
export class SqlMatchingResultComponent implements OnChanges {

  @Input() matchingResult: MatchingResult;

  successful: boolean;

  ngOnChanges(changes: SimpleChanges): void {
    this.successful = this.matchingResult.allMatches.every((m) => m.matchType === 'SUCCESSFUL_MATCH');
  }

}
