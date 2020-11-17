import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import {BoolFillOutComponent} from './bool-fill-out.component';
import {RandomSolveButtonsComponent} from '../../_components/random-solve-buttons/random-solve-buttons.component';
import {AppRoutingModule, routingComponents} from '../../../../app-routing.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

describe('BoolFilloutComponent', () => {
  let component: BoolFillOutComponent;
  let fixture: ComponentFixture<BoolFillOutComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule, AppRoutingModule],
      declarations: [BoolFillOutComponent, RandomSolveButtonsComponent, ...routingComponents]
    }).compileComponents();
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
