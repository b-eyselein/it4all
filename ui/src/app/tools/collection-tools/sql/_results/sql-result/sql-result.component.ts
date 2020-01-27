import {Component, Input} from '@angular/core';
import {IExpressionListMatchingResult, ISelectAdditionalComparisons, ISqlResult} from '../../sql-interfaces';

@Component({
  selector: 'it4all-sql-result',
  templateUrl: './sql-result.component.html'
})
export class SqlResultComponent {

  @Input() result: ISqlResult;

  getPoints(): number {
    const staticComp = this.result.staticComparison;

    const insertComp: IExpressionListMatchingResult | undefined = staticComp.additionalComparisons.insertComparison;
    const insertPoints = insertComp ? insertComp.points : 0;

    const selectComp: ISelectAdditionalComparisons | undefined = staticComp.additionalComparisons.selectComparisons;
    const selectPoints = selectComp ?
      selectComp.orderByComparison.points + selectComp.groupByComparison.points + selectComp.limitComparison.points : 0;

    return staticComp.columnComparison.points +
      staticComp.tableComparison.points +
      staticComp.whereComparison.points +
      staticComp.joinExpressionComparison.points +
      insertPoints +
      selectPoints;
  }

  getMaxPoints(): number {
    const staticComp = this.result.staticComparison;

    const insertComp: IExpressionListMatchingResult | undefined = staticComp.additionalComparisons.insertComparison;
    const insertPoints = insertComp ? insertComp.maxPoints : 0;

    const selectComp = staticComp.additionalComparisons.selectComparisons;
    const selectPoints = selectComp ?
      selectComp.orderByComparison.maxPoints + selectComp.groupByComparison.maxPoints + selectComp.limitComparison.maxPoints : 0;

    return staticComp.columnComparison.maxPoints +
      staticComp.tableComparison.maxPoints +
      staticComp.whereComparison.maxPoints +
      staticComp.joinExpressionComparison.maxPoints +
      insertPoints +
      selectPoints;

  }
}
