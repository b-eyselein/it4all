import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ExerciseFileCardComponent } from './exercise-file-card.component';

describe('ExerciseFileCardComponent', () => {
  let component: ExerciseFileCardComponent;
  let fixture: ComponentFixture<ExerciseFileCardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ExerciseFileCardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ExerciseFileCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
