import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {MrFragment} from '../../sql-apollo-mutations.service';
import {MatchType} from '../../../../../_interfaces/graphql-types';

@Component({
  selector: 'it4all-sql-matching-result',
  templateUrl: './sql-matching-result.component.html'
})
export class SqlMatchingResultComponent implements OnChanges {

  @Input() matchName: string;
  @Input() matchSingularName: string;

  @Input() matchingResult: MrFragment;

  successful: boolean;

  ngOnChanges(changes: SimpleChanges): void {
    this.successful = this.matchingResult.allMatches.every((m) => m.matchType === MatchType.SuccessfulMatch);
  }

}
