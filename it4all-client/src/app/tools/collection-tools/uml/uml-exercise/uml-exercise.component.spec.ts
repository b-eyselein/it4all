import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UmlExerciseComponent } from './uml-exercise.component';

describe('UmlExerciseComponent', () => {
  let component: UmlExerciseComponent;
  let fixture: ComponentFixture<UmlExerciseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UmlExerciseComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UmlExerciseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
