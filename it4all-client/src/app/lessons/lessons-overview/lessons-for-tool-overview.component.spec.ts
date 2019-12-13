import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {LessonsForToolOverviewComponent} from './lessons-for-tool-overview.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterTestingModule} from '@angular/router/testing';
import {LessonApiService} from '../_services/lesson-api.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ActivatedRoute} from '@angular/router';

describe('LessonsForToolOverviewComponent', () => {
  let component: LessonsForToolOverviewComponent;
  let fixture: ComponentFixture<LessonsForToolOverviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule, RouterTestingModule, HttpClientTestingModule],
      declarations: [LessonsForToolOverviewComponent],
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
    fixture = TestBed.createComponent(LessonsForToolOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
