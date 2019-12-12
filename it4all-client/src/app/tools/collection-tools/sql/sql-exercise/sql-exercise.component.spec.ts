import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {SqlExerciseComponent} from './sql-exercise.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CodemirrorModule} from '@ctrl/ngx-codemirror';
import {SqlResultComponent} from '../sql-result/sql-result.component';
import {SqlMatchingResultComponent} from '../sql-matching-result/sql-matching-result.component';
import {SolutionSavedComponent} from '../../../../shared/solution-saved/solution-saved.component';
import {PointsNotificationComponent} from '../../../../shared/points-notification/points-notification.component';
import {ApiService} from '../../_services/api.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';

describe('SqlExerciseComponent', () => {
  let component: SqlExerciseComponent;
  let fixture: ComponentFixture<SqlExerciseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule, CodemirrorModule, HttpClientTestingModule],
      declarations: [
        SqlExerciseComponent,
        SqlResultComponent, SqlMatchingResultComponent,
        SolutionSavedComponent, PointsNotificationComponent
      ],
      providers: [ApiService]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SqlExerciseComponent);
    component = fixture.componentInstance;
    component.exercise = {
      id: 0, collectionId: 0, toolId: 'sql', title: '', authors: [], text: '',
      semanticVersion: {major: 0, minor: 1, patch: 0}, tags: [],
      content: {}
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
