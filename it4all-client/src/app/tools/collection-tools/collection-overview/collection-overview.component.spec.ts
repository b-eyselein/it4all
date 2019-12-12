import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {CollectionOverviewComponent} from './collection-overview.component';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {CollectionToolRoutingModule} from '../collection-tools.routing';
import {CodemirrorModule} from '@ctrl/ngx-codemirror';
import {CollectionToolOverviewComponent} from '../collection-tool-overview/collection-tool-overview.component';

describe('CollectionOverviewComponent', () => {
  let component: CollectionOverviewComponent;
  let fixture: ComponentFixture<CollectionOverviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, CollectionToolRoutingModule, CodemirrorModule],
      declarations: [CollectionOverviewComponent, CollectionToolOverviewComponent]
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
