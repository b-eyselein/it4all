import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {LtiComponent} from './lti.component';
import {AppRoutingModule} from '../app-routing.module';

describe('LtiComponent', () => {
  let component: LtiComponent;
  let fixture: ComponentFixture<LtiComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [LtiComponent],
      imports: [AppRoutingModule]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LtiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
