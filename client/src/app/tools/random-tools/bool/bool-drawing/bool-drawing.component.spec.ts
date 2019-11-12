import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {BoolDrawingComponent} from './bool-drawing.component';
import {AppRoutingModule} from '../../../../app-routing.module';

describe('BoolDrawingComponent', () => {
  let component: BoolDrawingComponent;
  let fixture: ComponentFixture<BoolDrawingComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [BoolDrawingComponent],
      imports: [AppRoutingModule]
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
