import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BoolDrawingComponent } from './bool-drawing.component';

describe('BoolDrawingComponent', () => {
  let component: BoolDrawingComponent;
  let fixture: ComponentFixture<BoolDrawingComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BoolDrawingComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BoolDrawingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
