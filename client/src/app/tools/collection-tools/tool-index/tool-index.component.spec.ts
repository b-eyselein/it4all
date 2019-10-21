import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ToolIndexComponent } from './tool-index.component';

describe('ToolOverviewComponent', () => {
  let component: ToolIndexComponent;
  let fixture: ComponentFixture<ToolIndexComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ToolIndexComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ToolIndexComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
