import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {RandomOverviewComponent} from './random-overview.component';
import {AppRoutingModule, routingComponents} from '../../../app-routing.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

describe('RandomOverviewComponent', () => {
  let component: RandomOverviewComponent;
  let fixture: ComponentFixture<RandomOverviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule, AppRoutingModule],
      declarations: [RandomOverviewComponent, ...routingComponents]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RandomOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
