import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ToolTutorialsOverviewComponent } from './tool-tutorials-overview.component';

describe('ToolTutorialsOverviewComponent', () => {
  let component: ToolTutorialsOverviewComponent;
  let fixture: ComponentFixture<ToolTutorialsOverviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ToolTutorialsOverviewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ToolTutorialsOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
