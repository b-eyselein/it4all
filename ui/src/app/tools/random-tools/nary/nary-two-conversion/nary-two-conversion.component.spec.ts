import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import {NaryTwoConversionComponent} from './nary-two-conversion.component';
import {FormsModule} from '@angular/forms';
import {NaryNumberReadOnlyInputComponent} from '../_components/nary-number-read-only-input/nary-number-read-only-input.component';
import {RandomSolveButtonsComponent} from '../../_components/random-solve-buttons/random-solve-buttons.component';

describe('TwoConversionComponent', () => {
  let component: NaryTwoConversionComponent;
  let fixture: ComponentFixture<NaryTwoConversionComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [NaryTwoConversionComponent, NaryNumberReadOnlyInputComponent, RandomSolveButtonsComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NaryTwoConversionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
