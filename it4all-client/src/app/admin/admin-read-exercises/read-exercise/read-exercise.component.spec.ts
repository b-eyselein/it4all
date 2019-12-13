import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ReadExerciseComponent} from './read-exercise.component';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ApiService} from '../../../tools/collection-tools/_services/api.service';

describe('ReadExerciseComponent', () => {
  let component: ReadExerciseComponent;
  let fixture: ComponentFixture<ReadExerciseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [ReadExerciseComponent],
      providers: [ApiService]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReadExerciseComponent);
    component = fixture.componentInstance;
    component.exercise = {
      id: 1, collectionId: 1, toolId: 'programming', semanticVersion: {major: 1, minor: 0, patch: 0},
      title: '', authors: [], text: '', tags: [], content: {}
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
