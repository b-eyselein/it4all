import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import {BoolDrawingComponent} from './bool-drawing.component';
import {AppRoutingModule, routingComponents} from '../../../../app-routing.module';
import {ToolOverviewComponent} from '../../../../tool-overview/tool-overview.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

describe('BoolDrawingComponent', () => {
  let component: BoolDrawingComponent;
  let fixture: ComponentFixture<BoolDrawingComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule, AppRoutingModule],
      declarations: [BoolDrawingComponent, ToolOverviewComponent, ...routingComponents],
    }).compileComponents();
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
