import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { FlaskExerciseComponent } from './flask-exercise.component';

describe('FlaskExerciseComponent', () => {
  let component: FlaskExerciseComponent;
  let fixture: ComponentFixture<FlaskExerciseComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ FlaskExerciseComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FlaskExerciseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
