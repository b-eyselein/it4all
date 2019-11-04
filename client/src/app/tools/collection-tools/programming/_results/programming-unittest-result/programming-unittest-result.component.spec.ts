import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProgrammingUnittestResultComponent } from './programming-unittest-result.component';

describe('ProgrammingUnittestResultComponent', () => {
  let component: ProgrammingUnittestResultComponent;
  let fixture: ComponentFixture<ProgrammingUnittestResultComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProgrammingUnittestResultComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProgrammingUnittestResultComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
