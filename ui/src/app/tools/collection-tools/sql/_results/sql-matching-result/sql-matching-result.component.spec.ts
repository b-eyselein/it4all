import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import {SqlMatchingResultComponent} from './sql-matching-result.component';

describe('SqlMatchingResultComponent', () => {
  let component: SqlMatchingResultComponent;
  let fixture: ComponentFixture<SqlMatchingResultComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [SqlMatchingResultComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SqlMatchingResultComponent);
    component = fixture.componentInstance;
    component.matchingResult = {
      matchName: '',
      matchSingularName: '',
      points: -1,
      maxPoints: -1,
      allMatches: [],
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
