import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SqlExecutionResultComponent } from './sql-execution-result.component';

describe('SqlExecutionResultComponent', () => {
  let component: SqlExecutionResultComponent;
  let fixture: ComponentFixture<SqlExecutionResultComponent>;

  beforeEach(async(() => {
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
