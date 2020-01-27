import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminLessonsIndexComponent } from './admin-lessons-index.component';

describe('AdminLessonsIndexComponent', () => {
  let component: AdminLessonsIndexComponent;
  let fixture: ComponentFixture<AdminLessonsIndexComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AdminLessonsIndexComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminLessonsIndexComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
