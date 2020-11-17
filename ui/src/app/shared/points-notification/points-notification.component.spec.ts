import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import {PointsNotificationComponent} from './points-notification.component';

describe('PointsNotificationComponent', () => {
  let component: PointsNotificationComponent;
  let fixture: ComponentFixture<PointsNotificationComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [PointsNotificationComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PointsNotificationComponent);
    component = fixture.componentInstance;
    component.points = 1;
    component.maxPoints = 5;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
