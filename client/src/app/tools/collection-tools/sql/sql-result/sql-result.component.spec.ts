import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SqlResultComponent } from './sql-result.component';

describe('SqlResultComponent', () => {
  let component: SqlResultComponent;
  let fixture: ComponentFixture<SqlResultComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SqlResultComponent ]
    })
    .compileComponents();
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
