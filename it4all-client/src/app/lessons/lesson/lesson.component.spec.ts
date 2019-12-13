import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {LessonComponent} from './lesson.component';
import {LessonTextContentComponent} from '../lesson-text-content/lesson-text-content.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterTestingModule} from '@angular/router/testing';
import {LessonApiService} from '../_services/lesson-api.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ActivatedRoute} from '@angular/router';

describe('LessonComponent', () => {
  let component: LessonComponent;
  let fixture: ComponentFixture<LessonComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule, RouterTestingModule, HttpClientTestingModule],
      declarations: [LessonComponent, LessonTextContentComponent],
      providers: [
        LessonApiService,
        {
          provide: ActivatedRoute,
          useValue: {snapshot: {paramMap: new Map<string, string>([['toolId', 'web']])}}
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LessonComponent);
    component = fixture.componentInstance;
    component.lesson = {
      dependsOn: [],
      description: '',
      id: 1,
      title: '',
      content: []
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
