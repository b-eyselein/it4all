import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { UmlDiagDrawingCorrectionComponent } from './uml-diag-drawing-correction.component';

describe('UmlDiagDrawingCorrectionComponent', () => {
  let component: UmlDiagDrawingCorrectionComponent;
  let fixture: ComponentFixture<UmlDiagDrawingCorrectionComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ UmlDiagDrawingCorrectionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UmlDiagDrawingCorrectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
