import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {SqlExerciseComponent} from './sql-exercise.component';

describe('SqlExerciseComponent', () => {
  let component: SqlExerciseComponent;
  let fixture: ComponentFixture<SqlExerciseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SqlExerciseComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SqlExerciseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
