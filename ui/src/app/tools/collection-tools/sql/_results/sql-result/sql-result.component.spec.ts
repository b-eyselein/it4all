import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import {SqlResultComponent} from './sql-result.component';
import {SolutionSavedComponent} from '../../../../../shared/solution-saved/solution-saved.component';
import {PointsNotificationComponent} from '../../../../../shared/points-notification/points-notification.component';
import {SqlMatchingResultComponent} from '../sql-matching-result/sql-matching-result.component';

describe('SqlResultComponent', () => {
  let component: SqlResultComponent;
  let fixture: ComponentFixture<SqlResultComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [SqlResultComponent, SolutionSavedComponent, PointsNotificationComponent, SqlMatchingResultComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SqlResultComponent);
    component = fixture.componentInstance;
    component.result = {
      solutionSaved: true,
      points: 0,
      maxPoints: 0,
      staticComparison: {
        whereComparison: {maxPoints: -1, points: -1, allMatches: []},
        joinExpressionComparison: {maxPoints: -1, points: -1, allMatches: []},
        tableComparison: {maxPoints: -1, points: -1, allMatches: []},
        columnComparison: {maxPoints: -1, points: -1, allMatches: []},
        additionalComparisons: {}
      },
      executionResult: {
        sampleResult: null,
        userResult: null
      }
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
