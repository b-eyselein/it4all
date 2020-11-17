import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import {SqlExerciseComponent} from './sql-exercise.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CodemirrorModule} from '@ctrl/ngx-codemirror';
import {SqlResultComponent} from '../_results/sql-result/sql-result.component';
import {SqlMatchingResultComponent} from '../_results/sql-matching-result/sql-matching-result.component';
import {SolutionSavedComponent} from '../../../../shared/solution-saved/solution-saved.component';
import {PointsNotificationComponent} from '../../../../shared/points-notification/points-notification.component';
import {HttpClientTestingModule} from '@angular/common/http/testing';

describe('SqlExerciseComponent', () => {
  let component: SqlExerciseComponent;
  let fixture: ComponentFixture<SqlExerciseComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule, CodemirrorModule, HttpClientTestingModule],
      declarations: [
        SqlExerciseComponent,
        SqlResultComponent, SqlMatchingResultComponent,
        SolutionSavedComponent, PointsNotificationComponent
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SqlExerciseComponent);
    component = fixture.componentInstance;
    component.exerciseFragment = {id: 0, collectionId: 0, toolId: 'sql', title: '', text: ''};
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
