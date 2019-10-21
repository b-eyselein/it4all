import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RegexExerciseComponent } from './regex-exercise.component';

describe('RegexExerciseComponent', () => {
  let component: RegexExerciseComponent;
  let fixture: ComponentFixture<RegexExerciseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RegexExerciseComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RegexExerciseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
