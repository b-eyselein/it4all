import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SqlTableContentsComponent } from './sql-table-contents.component';

describe('SqlTableContentsComponent', () => {
  let component: SqlTableContentsComponent;
  let fixture: ComponentFixture<SqlTableContentsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SqlTableContentsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SqlTableContentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
