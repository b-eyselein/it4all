import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RandomOverviewComponent } from './random-overview.component';

describe('RandomOverviewComponent', () => {
  let component: RandomOverviewComponent;
  let fixture: ComponentFixture<RandomOverviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RandomOverviewComponent ]
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
