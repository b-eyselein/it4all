import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {MatchingResult} from "../../../../../_interfaces/graphql-types";

@Component({
  selector: 'it4all-sql-matching-result',
  templateUrl: './sql-matching-result.component.html'
})
export class SqlMatchingResultComponent implements OnChanges {

  @Input() matchName: string;
  @Input() matchSingularName: string;

  @Input() matchingResult: MatchingResult;

  successful: boolean;

  ngOnChanges(changes: SimpleChanges): void {
    this.successful = this.matchingResult.allMatches.every((m) => m.matchType === 'SUCCESSFUL_MATCH');
  }

}
