import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BoolExerciseComponent } from './bool-exercise.component';

describe('BoolExerciseComponent', () => {
  let component: BoolExerciseComponent;
  let fixture: ComponentFixture<BoolExerciseComponent>;

  beforeEach(async(() => {
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
