import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { SqlExecutionResultComponent } from './sql-execution-result.component';

describe('SqlExecutionResultComponent', () => {
  let component: SqlExecutionResultComponent;
  let fixture: ComponentFixture<SqlExecutionResultComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ SqlExecutionResultComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SqlExecutionResultComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
