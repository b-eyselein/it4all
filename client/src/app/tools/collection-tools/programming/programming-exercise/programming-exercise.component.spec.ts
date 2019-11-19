import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ProgrammingExerciseComponent} from './programming-exercise.component';
import {ExerciseFilesEditorComponent} from '../../tool-helpers/exercise-files-editor/exercise-files-editor.component';

describe('ProgrammingExerciseComponent', () => {
  let component: ProgrammingExerciseComponent;
  let fixture: ComponentFixture<ProgrammingExerciseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ProgrammingExerciseComponent, ExerciseFilesEditorComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProgrammingExerciseComponent);
    component = fixture.componentInstance;
    component.exerciseFiles = [];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
