import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NaryConversionComponent } from './nary-conversion.component';

describe('NaryConversionComponent', () => {
  let component: NaryConversionComponent;
  let fixture: ComponentFixture<NaryConversionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NaryConversionComponent ]
    })
    .compileComponents();
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
