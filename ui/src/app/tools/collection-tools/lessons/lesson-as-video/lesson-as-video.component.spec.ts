import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LessonAsVideoComponent } from './lesson-as-video.component';

describe('LessonAsVideoComponent', () => {
  let component: LessonAsVideoComponent;
  let fixture: ComponentFixture<LessonAsVideoComponent>;

  beforeEach(async(() => {
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
