import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {SqlResultComponent} from './sql-result.component';
import {SolutionSavedComponent} from '../../../../shared/solution-saved/solution-saved.component';
import {PointsNotificationComponent} from '../../../../shared/points-notification/points-notification.component';

describe('SqlResultComponent', () => {
  let component: SqlResultComponent;
  let fixture: ComponentFixture<SqlResultComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SqlResultComponent, SolutionSavedComponent, PointsNotificationComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SqlResultComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
