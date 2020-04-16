import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {CollectionOverviewComponent} from './collection-overview.component';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {CodemirrorModule} from '@ctrl/ngx-codemirror';
import {RouterTestingModule} from '@angular/router/testing';
import {TagComponent} from '../_components/tag/tag.component';
import {ActivatedRoute} from '@angular/router';

describe('CollectionOverviewComponent', () => {
  let component: CollectionOverviewComponent;
  let fixture: ComponentFixture<CollectionOverviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), CodemirrorModule],
      declarations: [CollectionOverviewComponent, TagComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {snapshot: {paramMap: new Map<string, string>([['toolId', 'web']])}}
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CollectionOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
