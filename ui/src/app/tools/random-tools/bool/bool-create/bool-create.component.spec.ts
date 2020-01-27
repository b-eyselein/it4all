import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {BoolCreateComponent} from './bool-create.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RandomSolveButtonsComponent} from '../../_components/random-solve-buttons/random-solve-buttons.component';
import {BoolCreateInstructionsComponent} from './bool-create-instructions/bool-create-instructions.component';
import {AppRoutingModule, routingComponents} from '../../../../app-routing.module';

describe('BoolCreateComponent', () => {
  let component: BoolCreateComponent;
  let fixture: ComponentFixture<BoolCreateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule, AppRoutingModule],
      declarations: [BoolCreateComponent, RandomSolveButtonsComponent, BoolCreateInstructionsComponent, ...routingComponents]
    }).compileComponents();
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
