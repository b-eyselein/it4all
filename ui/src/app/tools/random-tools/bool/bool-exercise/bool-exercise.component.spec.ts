import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { BoolExerciseComponent } from './bool-exercise.component';

describe('BoolExerciseComponent', () => {
  let component: BoolExerciseComponent;
  let fixture: ComponentFixture<BoolExerciseComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ BoolExerciseComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BoolExerciseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
