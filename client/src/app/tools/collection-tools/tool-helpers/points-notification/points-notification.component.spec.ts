import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {PointsNotificationComponent} from './points-notification.component';

describe('PointsNotificationComponent', () => {
  let component: PointsNotificationComponent;
  let fixture: ComponentFixture<PointsNotificationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PointsNotificationComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PointsNotificationComponent);
    component = fixture.componentInstance;
    component.result = {
      solutionSaved: true,
      isSuccessful: false,
      points: 1,
      maxPoints: 5
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
