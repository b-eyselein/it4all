import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AllExercisesOverviewComponent} from './all-exercises-overview.component';
import {TagComponent} from '../_components/tag/tag.component';
import {RouterTestingModule} from '@angular/router/testing';
import {ApiService} from '../_services/api.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ActivatedRoute} from '@angular/router';

describe('AllExercisesOverviewComponent', () => {
  let component: AllExercisesOverviewComponent;
  let fixture: ComponentFixture<AllExercisesOverviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      declarations: [AllExercisesOverviewComponent, TagComponent],
      providers: [
        ApiService,
        {
          provide: ActivatedRoute,
          useValue: {snapshot: {paramMap: new Map<string, string>([['toolId', 'web']])}}
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AllExercisesOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});