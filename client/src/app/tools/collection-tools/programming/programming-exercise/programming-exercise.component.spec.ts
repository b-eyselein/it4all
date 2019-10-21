import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProgrammingExerciseComponent } from './programming-exercise.component';

describe('ProgrammingExerciseComponent', () => {
  let component: ProgrammingExerciseComponent;
  let fixture: ComponentFixture<ProgrammingExerciseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProgrammingExerciseComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProgrammingExerciseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
