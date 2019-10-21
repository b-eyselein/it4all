import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BoolCreateComponent } from './bool-create.component';

describe('BoolCreateComponent', () => {
  let component: BoolCreateComponent;
  let fixture: ComponentFixture<BoolCreateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BoolCreateComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BoolCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
