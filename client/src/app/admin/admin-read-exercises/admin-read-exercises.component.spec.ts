import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminReadExercisesComponent } from './admin-read-exercises.component';

describe('AdminReadExercisesComponent', () => {
  let component: AdminReadExercisesComponent;
  let fixture: ComponentFixture<AdminReadExercisesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AdminReadExercisesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminReadExercisesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
