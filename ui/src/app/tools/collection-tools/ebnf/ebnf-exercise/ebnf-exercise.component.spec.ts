import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EbnfExerciseComponent } from './ebnf-exercise.component';

describe('EbnfExerciseComponent', () => {
  let component: EbnfExerciseComponent;
  let fixture: ComponentFixture<EbnfExerciseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EbnfExerciseComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EbnfExerciseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
