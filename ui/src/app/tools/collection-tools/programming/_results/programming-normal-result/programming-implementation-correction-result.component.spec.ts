import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ProgrammingImplementationCorrectionResultComponent } from './programming-implementation-correction-result.component';

describe('ProgrammingNormalResultComponent', () => {
  let component: ProgrammingImplementationCorrectionResultComponent;
  let fixture: ComponentFixture<ProgrammingImplementationCorrectionResultComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ ProgrammingImplementationCorrectionResultComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProgrammingImplementationCorrectionResultComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
