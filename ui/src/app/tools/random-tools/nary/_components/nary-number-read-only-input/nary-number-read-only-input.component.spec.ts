import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import {NaryNumberReadOnlyInputComponent} from './nary-number-read-only-input.component';
import {FormsModule} from '@angular/forms';
import {BINARY_SYSTEM} from '../../nary';

describe('NaryNumberInputComponent', () => {
  let component: NaryNumberReadOnlyInputComponent;
  let fixture: ComponentFixture<NaryNumberReadOnlyInputComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [NaryNumberReadOnlyInputComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NaryNumberReadOnlyInputComponent);
    component = fixture.componentInstance;
    component.naryNumberInput = {
      fieldId: '',
      maxValueForDigits: 256,
      numberingSystem: BINARY_SYSTEM,
      labelContent: '',
      decimalNumber: 5
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
