import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {LtiComponent} from './lti.component';
import {AppRoutingModule} from '../app-routing.module';
import {ToolOverviewComponent} from '../tool-overview/tool-overview.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

describe('LtiComponent', () => {
  let component: LtiComponent;
  let fixture: ComponentFixture<LtiComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule, AppRoutingModule],
      declarations: [LtiComponent, ToolOverviewComponent],
    }).compileComponents();
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
