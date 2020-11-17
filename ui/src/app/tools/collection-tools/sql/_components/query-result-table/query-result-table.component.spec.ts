import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { QueryResultTableComponent } from './query-result-table.component';

describe('QueryResultTableComponent', () => {
  let component: QueryResultTableComponent;
  let fixture: ComponentFixture<QueryResultTableComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ QueryResultTableComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(QueryResultTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
