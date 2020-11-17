import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { BoolCreateInstructionsComponent } from './bool-create-instructions.component';

describe('BoolCreateInstructionsComponent', () => {
  let component: BoolCreateInstructionsComponent;
  let fixture: ComponentFixture<BoolCreateInstructionsComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ BoolCreateInstructionsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BoolCreateInstructionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
