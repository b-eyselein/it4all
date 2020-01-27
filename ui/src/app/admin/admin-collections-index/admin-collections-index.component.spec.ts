import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminCollectionsIndexComponent } from './admin-collections-index.component';

describe('AdminCollectionsIndexComponent', () => {
  let component: AdminCollectionsIndexComponent;
  let fixture: ComponentFixture<AdminCollectionsIndexComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AdminCollectionsIndexComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminCollectionsIndexComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
