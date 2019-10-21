import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BoolFillOutComponent } from './bool-fill-out.component';

describe('BoolFilloutComponent', () => {
  let component: BoolFillOutComponent;
  let fixture: ComponentFixture<BoolFillOutComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BoolFillOutComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BoolFillOutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
