import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NaryNumberInputComponent } from './nary-number-input.component';

describe('NaryNumberInputComponent', () => {
  let component: NaryNumberInputComponent;
  let fixture: ComponentFixture<NaryNumberInputComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NaryNumberInputComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NaryNumberInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
