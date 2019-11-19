import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ReadExerciseComponent} from './read-exercise.component';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ProgrammingTool} from '../../../tools/collection-tools/programming-tool';

describe('ReadExerciseComponent', () => {
  let component: ReadExerciseComponent;
  let fixture: ComponentFixture<ReadExerciseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [ReadExerciseComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReadExerciseComponent);
    component = fixture.componentInstance;
    component.tool = ProgrammingTool;
    component.collection = {id: 1, toolId: 'programming', title: '', author: '', text: '', state: '', shortName: '', exercisesBasics: []};
    component.exercise = {
      id: 1,
      collId: 1,
      toolId: 'programming',
      semanticVersion: {major: 1, minor: 0, patch: 0},
      title: '',
      author: '',
      text: '',
      state: ''
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
