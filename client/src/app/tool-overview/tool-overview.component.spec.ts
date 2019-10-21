import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ToolOverviewComponent } from './tool-overview.component';

describe('OverviewComponent', () => {
  let component: ToolOverviewComponent;
  let fixture: ComponentFixture<ToolOverviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ToolOverviewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ToolOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
