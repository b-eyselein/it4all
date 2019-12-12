import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {LessonComponent} from './lesson.component';
import {LessonTextContentComponent} from '../lesson-text-content/lesson-text-content.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {lessonsRoutingComponents, LessonsRoutingModule} from '../lessons.routing';

describe('LessonComponent', () => {
  let component: LessonComponent;
  let fixture: ComponentFixture<LessonComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule, LessonsRoutingModule],
      declarations: [LessonComponent, LessonTextContentComponent, ...lessonsRoutingComponents]
    }).compileComponents();
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
