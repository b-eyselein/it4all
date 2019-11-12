import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {LessonComponent} from './lesson.component';
import {LessonTextContentComponent} from '../lesson-text-content/lesson-text-content.component';

describe('LessonComponent', () => {
  let component: LessonComponent;
  let fixture: ComponentFixture<LessonComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [LessonComponent, LessonTextContentComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LessonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
