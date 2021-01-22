import {Component, Input, ViewEncapsulation} from '@angular/core';
import {
  BinaryExpressionComparisonFragment,
  ColumnComparisonFragment,
  SelectAdditionalComparisonFragment,
  SqlResultFragment,
  StringMatchingResultFragment,
} from '../../sql-apollo-mutations.service';

@Component({
  selector: 'it4all-sql-result',
  templateUrl: './sql-result.component.html',
  encapsulation: ViewEncapsulation.None
})
export class SqlResultComponent {

  @Input() result: SqlResultFragment;

  points: number;
  maxPoints: number;

  get columnComparison(): ColumnComparisonFragment {
    return this.result.staticComparison.columnComparison;
  }

  get tableComparison(): StringMatchingResultFragment {
    return this.result.staticComparison.tableComparison;
  }

  get whereComparison(): BinaryExpressionComparisonFragment {
    return this.result.staticComparison.whereComparison;
  }

  get joinExpressionComparison(): BinaryExpressionComparisonFragment {
    return this.result.staticComparison.joinExpressionComparison;
  }

  get selectAdditionalComparisons(): SelectAdditionalComparisonFragment {
    return this.result.staticComparison.additionalComparisons.selectComparisons;
  }

  get insertAdditionalComparisons(): StringMatchingResultFragment {
    return this.result.staticComparison.additionalComparisons.insertComparison;
  }

}
