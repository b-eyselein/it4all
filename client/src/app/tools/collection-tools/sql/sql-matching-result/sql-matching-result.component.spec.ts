import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {SqlMatchingResultComponent} from './sql-matching-result.component';

describe('SqlMatchingResultComponent', () => {
  let component: SqlMatchingResultComponent;
  let fixture: ComponentFixture<SqlMatchingResultComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SqlMatchingResultComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SqlMatchingResultComponent);
    component = fixture.componentInstance;
    component.matchingResult = {
      success: false,
      matchName: '', matchSingularName: '',
      points: -1, maxPoints: -1,
      matches: [],
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
