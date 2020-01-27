import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminReadLessonsComponent } from './admin-read-lessons.component';

describe('AdminReadLessonsComponent', () => {
  let component: AdminReadLessonsComponent;
  let fixture: ComponentFixture<AdminReadLessonsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AdminReadLessonsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminReadLessonsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
