import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LessonOverviewComponent } from './lesson-overview.component';

describe('LessonOverviewComponent', () => {
  let component: LessonOverviewComponent;
  let fixture: ComponentFixture<LessonOverviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LessonOverviewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LessonOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
