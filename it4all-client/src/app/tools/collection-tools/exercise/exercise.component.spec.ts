import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ExerciseComponent} from './exercise.component';
import {ProgrammingExerciseComponent} from '../programming/programming-exercise/programming-exercise.component';
import {WebExerciseComponent} from '../web/web-exercise/web-exercise.component';
import {RegexExerciseComponent} from '../regex/regex-exercise/regex-exercise.component';
import {UmlExerciseComponent} from '../uml/uml-exercise/uml-exercise.component';

describe('ExerciseComponent', () => {
  let component: ExerciseComponent;
  let fixture: ComponentFixture<ExerciseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        ExerciseComponent,
        ProgrammingExerciseComponent, WebExerciseComponent, RegexExerciseComponent, UmlExerciseComponent
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ExerciseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
