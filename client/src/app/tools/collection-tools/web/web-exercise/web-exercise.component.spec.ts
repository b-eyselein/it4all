import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WebExerciseComponent } from './web-exercise.component';

describe('WebExerciseComponent', () => {
  let component: WebExerciseComponent;
  let fixture: ComponentFixture<WebExerciseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WebExerciseComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WebExerciseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
