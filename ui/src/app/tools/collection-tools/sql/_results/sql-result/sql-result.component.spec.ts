import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {SqlResultComponent} from './sql-result.component';
import {SolutionSavedComponent} from '../../../../../shared/solution-saved/solution-saved.component';
import {PointsNotificationComponent} from '../../../../../shared/points-notification/points-notification.component';
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
      staticComparison: {
        whereComparison: {
          maxPoints: -1, points: -1, allMatches: [], matchSingularName: '', matchName: ''
        },
        joinExpressionComparison: {
          maxPoints: -1, points: -1, allMatches: [], matchSingularName: '', matchName: ''
        },
        tableComparison: {
          maxPoints: -1, points: -1, allMatches: [], matchSingularName: '', matchName: ''
        },
        columnComparison: {
          maxPoints: -1, points: -1, allMatches: [], matchSingularName: '', matchName: ''
        },
        additionalComparisons: {}
      },
      executionResult: {
        sampleResultTry: null,
        userResultTry: null
      }
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
