import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {InsertComparisonFragment, SelectAdditionalComparisonFragment, SqlResultFragment} from '../../sql-apollo-service';

@Component({
  selector: 'it4all-sql-result',
  templateUrl: './sql-result.component.html'
})
export class SqlResultComponent implements OnChanges {

  @Input() result: SqlResultFragment;

  points: number;
  maxPoints: number;

  ngOnChanges(changes: SimpleChanges): void {
    const staticComp = this.result.staticComparison;

    const columnPointsQuarters = staticComp.columnComparison.points.quarters;
    const columnMaxPointsQuarters = staticComp.columnComparison.maxPoints.quarters;

    const tablePointsQuarters = staticComp.tableComparison.points.quarters;
    const tableMaxPointsQuarters = staticComp.tableComparison.maxPoints.quarters;

    const wherePointsQuarters = staticComp.whereComparison.points.quarters;
    const whereMaxPointsQuarters = staticComp.whereComparison.maxPoints.quarters;

    const joinPointsQuarters = staticComp.joinExpressionComparison.points.quarters;
    const joinMaxPointsQuarters = staticComp.joinExpressionComparison.maxPoints.quarters;


    const selectComp: SelectAdditionalComparisonFragment | undefined = staticComp.additionalComparisons.selectComparisons;

    const groupByPointsQuarters = selectComp ? selectComp.groupByComparison.points.quarters : 0;
    const groupByMaxPointsQuarters = selectComp ? selectComp.groupByComparison.maxPoints.quarters : 0;

    const orderByPointsQuarters = selectComp ? selectComp.orderByComparison.points.quarters : 0;
    const orderByMaxPointsQuarters = selectComp ? selectComp.orderByComparison.maxPoints.quarters : 0;

    const limitPointsQuarters = selectComp ? selectComp.limitComparison.points.quarters : 0;
    const limitMaxPointsQuarters = selectComp ? selectComp.limitComparison.maxPoints.quarters : 0;


    const insertComp: InsertComparisonFragment | undefined = staticComp.additionalComparisons.insertComparison;

    const insertPointsQuarters = insertComp ? insertComp.points.quarters : 0;
    const insertMaxPointsQuarters = insertComp ? insertComp.maxPoints.quarters : 0;

    this.points = (
      columnPointsQuarters + tablePointsQuarters + wherePointsQuarters + joinPointsQuarters +
      groupByPointsQuarters + orderByPointsQuarters + limitPointsQuarters + insertPointsQuarters
    ) / 4;

    this.maxPoints = (
      columnMaxPointsQuarters + tableMaxPointsQuarters + whereMaxPointsQuarters + joinMaxPointsQuarters +
      groupByMaxPointsQuarters + orderByMaxPointsQuarters + limitMaxPointsQuarters + insertMaxPointsQuarters
    ) / 4;
  }

}
