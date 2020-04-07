import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminEditExerciseComponent } from './admin-edit-exercise.component';

describe('AdminEditExerciseComponent', () => {
  let component: AdminEditExerciseComponent;
  let fixture: ComponentFixture<AdminEditExerciseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AdminEditExerciseComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminEditExerciseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
