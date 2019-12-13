import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {NaryAdditionComponent} from './nary-addition.component';
import {FormsModule} from '@angular/forms';
import {NaryNumberReadOnlyInputComponent} from '../_components/nary-number-read-only-input/nary-number-read-only-input.component';
import {RandomSolveButtonsComponent} from '../../_components/random-solve-buttons/random-solve-buttons.component';

describe('NaryAdditionComponent', () => {
  let component: NaryAdditionComponent;
  let fixture: ComponentFixture<NaryAdditionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [NaryAdditionComponent, NaryNumberReadOnlyInputComponent, RandomSolveButtonsComponent],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NaryAdditionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
