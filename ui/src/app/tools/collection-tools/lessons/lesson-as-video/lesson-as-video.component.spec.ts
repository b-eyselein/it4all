import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { LessonAsVideoComponent } from './lesson-as-video.component';

describe('LessonAsVideoComponent', () => {
  let component: LessonAsVideoComponent;
  let fixture: ComponentFixture<LessonAsVideoComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ LessonAsVideoComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LessonAsVideoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
