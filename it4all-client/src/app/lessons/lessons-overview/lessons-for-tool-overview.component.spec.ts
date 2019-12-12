import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {LessonsForToolOverviewComponent} from './lessons-for-tool-overview.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {lessonsRoutingComponents, LessonsRoutingModule} from '../lessons.routing';

describe('LessonsOverviewComponent', () => {
  let component: LessonsForToolOverviewComponent;
  let fixture: ComponentFixture<LessonsForToolOverviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule, LessonsRoutingModule],
      declarations: [LessonsForToolOverviewComponent, ...lessonsRoutingComponents]
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
