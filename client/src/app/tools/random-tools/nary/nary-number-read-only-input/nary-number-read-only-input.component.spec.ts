import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NaryNumberReadOnlyInputComponent } from './nary-number-read-only-input.component';

describe('NaryNumberInputComponent', () => {
  let component: NaryNumberReadOnlyInputComponent;
  let fixture: ComponentFixture<NaryNumberReadOnlyInputComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NaryNumberReadOnlyInputComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NaryNumberReadOnlyInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
