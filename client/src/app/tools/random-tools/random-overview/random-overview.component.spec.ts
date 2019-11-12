import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {RandomOverviewComponent} from './random-overview.component';
import {AppRoutingModule} from '../../../app-routing.module';

describe('RandomOverviewComponent', () => {
  let component: RandomOverviewComponent;
  let fixture: ComponentFixture<RandomOverviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RandomOverviewComponent],
      imports: [AppRoutingModule]
    })
      .compileComponents();
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
