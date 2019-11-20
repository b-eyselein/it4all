import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ExerciseOverviewComponent } from './exercise-overview.component';

describe('ExerciseOverviewComponent', () => {
  let component: ExerciseOverviewComponent;
  let fixture: ComponentFixture<ExerciseOverviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ExerciseOverviewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ExerciseOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
