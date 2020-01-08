import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LessonQuestionsContentComponent } from './lesson-questions-content.component';

describe('LessonQuestionsComponent', () => {
  let component: LessonQuestionsContentComponent;
  let fixture: ComponentFixture<LessonQuestionsContentComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LessonQuestionsContentComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LessonQuestionsContentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
