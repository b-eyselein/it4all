import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AllExercisesOverviewComponent } from './all-exercises-overview.component';

describe('AllExercisesOverviewComponent', () => {
  let component: AllExercisesOverviewComponent;
  let fixture: ComponentFixture<AllExercisesOverviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AllExercisesOverviewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AllExercisesOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
