import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {NaryConversionComponent} from './nary-conversion.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {NaryNumberReadOnlyInputComponent} from '../_components/nary-number-read-only-input/nary-number-read-only-input.component';
import {RandomSolveButtonsComponent} from '../../_components/random-solve-buttons/random-solve-buttons.component';

describe('NaryConversionComponent', () => {
  let component: NaryConversionComponent;
  let fixture: ComponentFixture<NaryConversionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule],
      declarations: [NaryConversionComponent, NaryNumberReadOnlyInputComponent, RandomSolveButtonsComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NaryConversionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});