import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ToolOverviewComponent} from './tool-overview.component';
import {AppRoutingModule, routingComponents} from '../app-routing.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

describe('ToolOverviewComponent', () => {
  let component: ToolOverviewComponent;
  let fixture: ComponentFixture<ToolOverviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule, AppRoutingModule],
      declarations: [ToolOverviewComponent, ...routingComponents]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ToolOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
