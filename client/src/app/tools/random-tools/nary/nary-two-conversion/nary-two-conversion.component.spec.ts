import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NaryTwoConversionComponent } from './nary-two-conversion.component';

describe('TwoConversionComponent', () => {
  let component: NaryTwoConversionComponent;
  let fixture: ComponentFixture<NaryTwoConversionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NaryTwoConversionComponent ]
    })
    .compileComponents();
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
