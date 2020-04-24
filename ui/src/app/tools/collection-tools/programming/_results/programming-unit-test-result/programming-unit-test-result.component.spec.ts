import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProgrammingUnitTestResultComponent } from './programming-unit-test-result.component';

describe('ProgrammingUnitTestResultComponent', () => {
  let component: ProgrammingUnitTestResultComponent;
  let fixture: ComponentFixture<ProgrammingUnitTestResultComponent>;

  beforeEach(async(() => {
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
