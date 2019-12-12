import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {SqlResultComponent} from './sql-result.component';
import {SolutionSavedComponent} from '../../../../shared/solution-saved/solution-saved.component';
import {PointsNotificationComponent} from '../../../../shared/points-notification/points-notification.component';
import {SqlMatchingResultComponent} from '../sql-matching-result/sql-matching-result.component';

describe('SqlResultComponent', () => {
  let component: SqlResultComponent;
  let fixture: ComponentFixture<SqlResultComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SqlResultComponent, SolutionSavedComponent, PointsNotificationComponent, SqlMatchingResultComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SqlResultComponent);
    component = fixture.componentInstance;
    component.result = {
      solutionSaved: true,
      maxPoints: -1,
      points: -1,
      isSuccessful: true,
      results: {
        whereComparisons: {
          maxPoints: -1, points: -1, matches: [], matchSingularName: '', success: true, matchName: ''
        },
        joinExpressionComparisons: {
          maxPoints: -1, points: -1, matches: [], matchSingularName: '', success: true, matchName: ''
        },
        tableComparisons: {
          maxPoints: -1, points: -1, matches: [], matchSingularName: '', success: true, matchName: ''
        },
        columnComparisons: {
          maxPoints: -1, points: -1, matches: [], matchSingularName: '', success: true, matchName: ''
        },
        additionalComparisons: [],
        executionResults: {
          sampleResult: null, success: 'ERROR', userResult: null
        },
        message: ''
      },
      success: ''
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
