import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NaryExerciseComponent } from './nary-exercise.component';

describe('NaryExerciseComponent', () => {
  let component: NaryExerciseComponent;
  let fixture: ComponentFixture<NaryExerciseComponent>;

  beforeEach(async(() => {
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
