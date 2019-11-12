import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {NaryNumberReadOnlyInputComponent} from './nary-number-read-only-input.component';
import {FormsModule} from '@angular/forms';

describe('NaryNumberInputComponent', () => {
  let component: NaryNumberReadOnlyInputComponent;
  let fixture: ComponentFixture<NaryNumberReadOnlyInputComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [NaryNumberReadOnlyInputComponent],
      imports: [FormsModule]
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
