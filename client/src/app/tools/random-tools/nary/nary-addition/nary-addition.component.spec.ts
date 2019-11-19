import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {NaryAdditionComponent} from './nary-addition.component';
import {FormsModule} from '@angular/forms';
import {NaryNumberReadOnlyInputComponent} from '../_components/nary-number-read-only-input/nary-number-read-only-input.component';
import {routingComponents} from '../../../../app-routing.module';

describe('NaryAdditionComponent', () => {
  let component: NaryAdditionComponent;
  let fixture: ComponentFixture<NaryAdditionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [NaryAdditionComponent, NaryNumberReadOnlyInputComponent, ...routingComponents],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NaryAdditionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
