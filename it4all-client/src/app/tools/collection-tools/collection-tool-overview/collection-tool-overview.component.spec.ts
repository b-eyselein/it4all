import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CollectionToolOverviewComponent } from './collection-tool-overview.component';

describe('ToolOverviewComponent', () => {
  let component: CollectionToolOverviewComponent;
  let fixture: ComponentFixture<CollectionToolOverviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CollectionToolOverviewComponent ]
    })
    .compileComponents();
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
