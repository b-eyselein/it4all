import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {InsertComparisonFragment, SelectAdditionalComparisonFragment, SqlResultFragment} from '../../sql-apollo-mutations.service';

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

    const columnPointsQuarters = staticComp.columnComparison.points;
    const columnMaxPointsQuarters = staticComp.columnComparison.maxPoints;

    const tablePointsQuarters = staticComp.tableComparison.points;
    const tableMaxPointsQuarters = staticComp.tableComparison.maxPoints;

    const wherePointsQuarters = staticComp.whereComparison.points;
    const whereMaxPointsQuarters = staticComp.whereComparison.maxPoints;

    const joinPointsQuarters = staticComp.joinExpressionComparison.points;
    const joinMaxPointsQuarters = staticComp.joinExpressionComparison.maxPoints;


    const selectComp: SelectAdditionalComparisonFragment | undefined = staticComp.additionalComparisons.selectComparisons;

    const groupByPointsQuarters = selectComp ? selectComp.groupByComparison.points : 0;
    const groupByMaxPointsQuarters = selectComp ? selectComp.groupByComparison.maxPoints : 0;

    const orderByPointsQuarters = selectComp ? selectComp.orderByComparison.points : 0;
    const orderByMaxPointsQuarters = selectComp ? selectComp.orderByComparison.maxPoints : 0;

    const limitPointsQuarters = selectComp ? selectComp.limitComparison.points : 0;
    const limitMaxPointsQuarters = selectComp ? selectComp.limitComparison.maxPoints : 0;


    const insertComp: InsertComparisonFragment | undefined = staticComp.additionalComparisons.insertComparison;

    const insertPointsQuarters = insertComp ? insertComp.points : 0;
    const insertMaxPointsQuarters = insertComp ? insertComp.maxPoints : 0;

    this.points = columnPointsQuarters + tablePointsQuarters + wherePointsQuarters + joinPointsQuarters + groupByPointsQuarters + orderByPointsQuarters + limitPointsQuarters + insertPointsQuarters;

    this.maxPoints = columnMaxPointsQuarters + tableMaxPointsQuarters + whereMaxPointsQuarters + joinMaxPointsQuarters + groupByMaxPointsQuarters + orderByMaxPointsQuarters + limitMaxPointsQuarters + insertMaxPointsQuarters;
  }

}
