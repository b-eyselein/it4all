import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ToolTutorialsOverviewComponent} from './tool-tutorials-overview.component';
import {AppRoutingModule, routingComponents} from '../../app-routing.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

describe('ToolTutorialsOverviewComponent', () => {
  let component: ToolTutorialsOverviewComponent;
  let fixture: ComponentFixture<ToolTutorialsOverviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule, AppRoutingModule],
      declarations: [ToolTutorialsOverviewComponent, ...routingComponents]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ToolTutorialsOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
