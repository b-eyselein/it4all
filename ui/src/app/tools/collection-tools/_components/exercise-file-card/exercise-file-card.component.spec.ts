import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import {ExerciseFileCardComponent} from './exercise-file-card.component';

describe('ExerciseFileCardComponent', () => {
  let component: ExerciseFileCardComponent;
  let fixture: ComponentFixture<ExerciseFileCardComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ExerciseFileCardComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ExerciseFileCardComponent);
    component = fixture.componentInstance;
    component.exerciseFile = {
      name: '',
      fileType: 'python',
      content: '',
      editable: false
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
