import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CollectionToolIndexComponent } from './collection-tool-index.component';

describe('ToolOverviewComponent', () => {
  let component: CollectionToolIndexComponent;
  let fixture: ComponentFixture<CollectionToolIndexComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CollectionToolIndexComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CollectionToolIndexComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
