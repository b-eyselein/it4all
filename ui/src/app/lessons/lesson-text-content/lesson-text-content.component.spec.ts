import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {LessonTextContentComponent} from './lesson-text-content.component';

describe('LessonTextContentComponent', () => {
  let component: LessonTextContentComponent;
  let fixture: ComponentFixture<LessonTextContentComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [LessonTextContentComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LessonTextContentComponent);
    component = fixture.componentInstance;
    component.content = {
      id: 1, lessonId: 1, toolId: 'web', _type: 'Text', content: '', priorSolved: true
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
