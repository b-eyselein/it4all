import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ReadExerciseComponent } from './read-exercise.component';

describe('ReadExerciseComponent', () => {
  let component: ReadExerciseComponent;
  let fixture: ComponentFixture<ReadExerciseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ReadExerciseComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReadExerciseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
