import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { NaryExerciseComponent } from './nary-exercise.component';

describe('NaryExerciseComponent', () => {
  let component: NaryExerciseComponent;
  let fixture: ComponentFixture<NaryExerciseComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ NaryExerciseComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NaryExerciseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
