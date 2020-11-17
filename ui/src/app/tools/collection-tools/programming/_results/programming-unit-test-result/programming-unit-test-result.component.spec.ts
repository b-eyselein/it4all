import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ProgrammingUnitTestResultComponent } from './programming-unit-test-result.component';

describe('ProgrammingUnitTestResultComponent', () => {
  let component: ProgrammingUnitTestResultComponent;
  let fixture: ComponentFixture<ProgrammingUnitTestResultComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ ProgrammingUnitTestResultComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProgrammingUnitTestResultComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
