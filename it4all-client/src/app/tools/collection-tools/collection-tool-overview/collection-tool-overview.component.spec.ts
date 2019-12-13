import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {CollectionToolOverviewComponent} from './collection-tool-overview.component';
import {RouterTestingModule} from '@angular/router/testing';

describe('CollectionToolOverviewComponent', () => {
  let component: CollectionToolOverviewComponent;
  let fixture: ComponentFixture<CollectionToolOverviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [CollectionToolOverviewComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CollectionToolOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
