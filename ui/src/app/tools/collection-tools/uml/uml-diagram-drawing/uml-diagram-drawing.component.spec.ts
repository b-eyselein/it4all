import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { UmlDiagramDrawingComponent } from './uml-diagram-drawing.component';

describe('UmlDiagramDrawingComponent', () => {
  let component: UmlDiagramDrawingComponent;
  let fixture: ComponentFixture<UmlDiagramDrawingComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ UmlDiagramDrawingComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UmlDiagramDrawingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
